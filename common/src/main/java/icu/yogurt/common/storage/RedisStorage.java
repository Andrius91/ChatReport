package icu.yogurt.common.storage;

import com.google.gson.Gson;
import icu.yogurt.common.connector.RedisConnector;
import icu.yogurt.common.interfaces.IChatReport;
import icu.yogurt.common.interfaces.Storage;
import icu.yogurt.common.model.Message;
import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;

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
        try(Jedis jedis = getRedisConnector().getResource()){
            String key = REDIS_KEY + player;
            return jedis.exists(key);
        }catch (Exception e){
            plugin.log(1, "Error checking if user exists: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void saveMessage(String player, Message message) {
        String json = gson.toJson(message);
        String key = REDIS_KEY + player;

        addMessage(key, json);
    }

    @SneakyThrows
    @Override
    public List<Message> getMessages(String player) {
        CompletableFuture<List<Message>> completableFuture = new CompletableFuture<>();
        List<Message> messages = new LinkedList<>();
        try(Jedis jedis = getRedisConnector().getResource()){
            String key = REDIS_KEY + player;

            messages = jedis.hvals(key)
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
    public String getStaffUUID(String username) {
        return getStaffMap().getOrDefault(username, "");
    }

    @Override
    public void updateStaffUUID(String username, String uuid) {
        try(Jedis jedis = getRedisConnector().getResource()){
            jedis.publish("pandora:staff", username + ":" + uuid);
        }catch(Exception e){
            plugin.log(1, "Failed to save staff uuid to Redis: " + e.getMessage());
        }
    }

    private void addMessage(String key, String value){
        try (Jedis jedis = getRedisConnector().getResource()) {
            // Delete the oldest field if the hash already has 10 fields
            long fieldCount = jedis.hlen(key);
            int MAX_MESSAGES = 25;
            if (fieldCount >= MAX_MESSAGES) {
                jedis.hkeys(key).stream()
                        .sorted()
                        .findFirst()
                        .ifPresent(oldestField -> jedis.hdel(key, oldestField));
            }

            // Add the message as a new field to the hash
            jedis.hset(key, "message:" + System.currentTimeMillis(), value);

            // Set the key to expire in 24 hours
            jedis.expire(key, 24 * 60 * 60);


        } catch (Exception e) {

            plugin.log(1, "Failed to save message to Redis: " + e.getMessage());
        }
    }

}
