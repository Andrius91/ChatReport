package icu.yogurt.common.cache;

import com.google.gson.Gson;
import icu.yogurt.common.connector.RedisConnector;
import icu.yogurt.common.interfaces.IChatReport;
import icu.yogurt.common.model.UserModel;
import redis.clients.jedis.Jedis;

public class UserCache {

    private final IChatReport plugin;
    private final Gson gson;
    private final RedisConnector redisConnector;
    private final String EXISTS_KEY = "chat-report:exists:";

    public UserCache(IChatReport plugin, RedisConnector redisConnector) {
        this.plugin = plugin;
        this.gson = plugin.gson;
        this.redisConnector = redisConnector;
    }

    private synchronized RedisConnector getRedisConnector() {
        return redisConnector;
    }

    public UserModel getCachedUserModel(String username) {
        try (Jedis jedis = getRedisConnector().getResource()) {
            String json = jedis.get(EXISTS_KEY + username.toLowerCase());
            return json != null ? gson.fromJson(json, UserModel.class) : null;
        } catch (Exception e) {
            plugin.log(1, "Failed to get cached UserModel: " + e.getMessage());
            return null;
        }
    }

    public void cacheUserModel(String username, UserModel userModel) {
        try (Jedis jedis = getRedisConnector().getResource()) {
            String json = gson.toJson(userModel);
            jedis.setex(EXISTS_KEY + username.toLowerCase(), 24 * 60 * 60, json);
        } catch (Exception e) {
            plugin.log(1, "Failed to cache UserModel: " + e.getMessage());
        }
    }
}