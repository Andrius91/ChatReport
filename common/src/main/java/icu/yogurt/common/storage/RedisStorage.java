package icu.yogurt.common.storage;

import com.google.gson.Gson;
import icu.yogurt.common.connector.RedisConnector;
import icu.yogurt.common.interfaces.IChatReport;
import icu.yogurt.common.interfaces.Storage;
import icu.yogurt.common.model.Message;
import icu.yogurt.common.model.UserModel;
import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RedisStorage implements Storage {

    private final IChatReport plugin;
    private final Gson gson = new Gson();
    private final RedisConnector redisConnector;
    private final String REDIS_KEY = "chat-report:";
    private final Map<String, String> staffMap = new HashMap<>();

    public RedisStorage(IChatReport plugin, RedisConnector redisConnector) {
        this.plugin = plugin;
        this.redisConnector = redisConnector;
        this.redisConnector.connect();
    }

    private synchronized RedisConnector getRedisConnector() {
        return redisConnector;
    }

    @Override
    public Map<String, String> getStaffMap() {
        return this.staffMap;
    }

    @Override
    public boolean playerExists(String player) {
        return plugin.getDatabase().verifyUserByUsername(player);
    }

    @Override
    public void saveMessage(Message message) {
        String json = gson.toJson(message);
        String key = REDIS_KEY + message.getSender();

        addMessage(key, json);
    }

    @SneakyThrows
    @Override
    public List<Message> getMessages(String player) {
        CompletableFuture<List<Message>> completableFuture = new CompletableFuture<>();
        List<Message> messages = new LinkedList<>();
        try(Jedis jedis = getRedisConnector().getResource()){
            String key = REDIS_KEY + player;

            messages = jedis.lrange(key, 0, -1)
                    .stream()
                    .map(x -> gson.fromJson(x, Message.class))
                    .sorted(Comparator.comparing(Message::getLocalDateTime))
                    .collect(Collectors.toList());
        }catch (Exception e){
           plugin.log(1, "Error getting redis messages: " + e.getMessage());
        }finally{
            completableFuture.complete(messages);
        }
        return completableFuture.get();
    }

    @Override
    public List<Message> getCombinedMessages(String player1, String player2, int limit){
        List<Message> player1Messages = getMessages(player1);
        List<Message> player2Messages = getMessages(player2);

        return Stream.concat(player1Messages.stream(), player2Messages.stream())
                .sorted(Comparator.comparing(Message::getLocalDateTime))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String getUserUUID(String username) {
        UserModel userModel = plugin.getDatabase().getUserByUsername(username);
        return userModel != null ? userModel.getUuid() : "0";
    }

    private void addMessage(String key, String value){
        try (Jedis jedis = getRedisConnector().getResource()) {
            // Delete the oldest field if the hash already has 10 fields
            long fieldCount = jedis.llen(key);
            int MAX_MESSAGES = 25;
            if (fieldCount >= MAX_MESSAGES) {
                jedis.lrange(key, 0, 0).stream()
                        .sorted()
                        .findFirst()
                        .ifPresent(oldestElement -> jedis.lrem(key, 0, oldestElement));
            }

            // Add the message as a new field to the hash
            jedis.rpush(key, value);

            // Set the key to expire in 24 hours
            jedis.expire(key, 24 * 60 * 60);

        } catch (Exception e) {

            plugin.log(1, "Failed to save message to Redis: " + e.getMessage());
        }
    }

    @Override
    public void saveMessages(List<Message> messages) {
        try (Jedis jedis = getRedisConnector().getResource()) {
            Transaction transaction = jedis.multi();
            Map<String, Response<Long>> lengthMap = new HashMap<>();

            for (Message message : messages) {
                String sender = message.getSender();
                String json = gson.toJson(message);
                String key = REDIS_KEY + sender;

                // Agregar el mensaje como un nuevo campo en el hash
                transaction.rpush(key, json);

                // Establecer el tiempo de expiración en 24 horas
                //transaction.expire(key, 24 * 60 * 60);
                transaction.expire(key, 120);

                // Obtener la longitud de la lista (se agregará a la transacción)
                Response<Long> length = transaction.llen(key);

                lengthMap.put(sender, length);
            }

            // Ejecutar la transacción y obtener los resultados
            transaction.exec();

            // Obtener el valor de la longitud de la lista para cada mensaje
            for (Message message : messages) {
                String sender = message.getSender();
                Response<Long> lengthResponse = lengthMap.get(sender);

                if (lengthResponse != null) {
                    long length = lengthResponse.get();

                    if (length > 25) {
                        // Eliminar los elementos más antiguos, dejando solo los últimos 25
                        jedis.ltrim(REDIS_KEY + sender, length - 25, -1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.log(1, "Failed to save messages to Redis: " + e.getMessage());
        }
    }
}
