package icu.yogurt.chatreport.common.connector;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@RequiredArgsConstructor
@Getter
@ToString
public class RedisConnector implements AutoCloseable{

    private JedisPool jedisPool;
    private final String url;

    public void connect() {
        if (jedisPool == null) {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(128);
            poolConfig.setMaxIdle(16);
            poolConfig.setMinIdle(8);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            jedisPool = new JedisPool(poolConfig, url);
        }
    }

    public Jedis getResource() {
        if (jedisPool == null) {
            connect();
        }
        return jedisPool.getResource();
    }


    @Override
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}
