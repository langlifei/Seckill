package com.znuel.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 11:13
 * @Describe
 */
@Service
public class RedisPool {

    //注入redis参数配置
    @Autowired
    private RedisConfig redisConfig;

    @Bean
    public JedisPool jedisPool(){
        //由于连接池通常会有配置，故先设置其参数
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        jedisPoolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,redisConfig.getHost(),redisConfig.getPort(),redisConfig.getTimeout()*1000,redisConfig.getPassword());//没有密码
        return jedisPool;
    }
}
