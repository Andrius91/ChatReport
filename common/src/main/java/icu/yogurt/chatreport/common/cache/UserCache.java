package icu.yogurt.chatreport.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.connector.RedisConnector;
import icu.yogurt.chatreport.common.model.UserModel;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

public class UserCache {

    private final BasePlugin plugin;
    private final Gson gson;
    private final RedisConnector redisConnector;
    private final String EXISTS_KEY = "chat-report:exists:";

    private final Cache<String, String> REPORT_CACHE;

    public UserCache(BasePlugin plugin, RedisConnector redisConnector) {
        this.plugin = plugin;
        this.gson = plugin.gson;
        this.redisConnector = redisConnector;
        this.REPORT_CACHE = CacheBuilder.newBuilder()
                .expireAfterWrite(4, TimeUnit.MINUTES)
                .build();
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

    public boolean isSavedInvalid(String player) {
        return REPORT_CACHE.getIfPresent(player) != null;
    }

    public void saveInvalid(String player) {
        REPORT_CACHE.put(player, "");
    }
}