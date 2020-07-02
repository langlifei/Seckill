package com.znuel.seckill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/20 16:16
 * @Describe
 */
@Service
public class RedisService {

    //由于JedisPool是一个资源池，所以可以被重复使用，不释放，故将其注入到spring容器中
    @Autowired
    private JedisPool jedisPool;

    /**
     * 获取对象
     * @param keyPrefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix ,String key,Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix()+key;
            String str = jedis.get(realKey);
            T t = stringToBean(str,clazz);
            return t;
        }finally {
            //由于使用池化技术，使用完需要释放连接资源，否则很快会被耗空
            returnToResource(jedis);
        }
    }


    public <T> Long setNX(KeyPrefix keyPrefix,String key,T value){
            Jedis jedis = null;
            Long flag = null;
            try {
                jedis = jedisPool.getResource();
                String str = beanToString(value);
                if(str==null||str.length()<=0)
                    return (long)0;
                String realKey = keyPrefix.getPrefix()+key;
                int expireSecond = keyPrefix.expireSeconds();
                flag  = jedis.setnx(realKey,str);
                if(expireSecond>0)
                    jedis.expire(realKey,expireSecond);
                return flag;
            }finally {
                returnToResource(jedis);
            }
    }

    /*
         删除
     */
    public <T> boolean delete(KeyPrefix keyPrefix ,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix()+key;
            Long del = jedis.del(realKey);
            return del>0;
        }finally {
            //由于使用池化技术，使用完需要释放连接资源，否则很快会被耗空
            returnToResource(jedis);
        }
    }
    /*
        设置对象
     */
    public <T> boolean set(KeyPrefix keyPrefix,String key,T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if(str==null||str.length()<=0)
                return false;
            String realKey = keyPrefix.getPrefix()+key;
            int expireSecond = keyPrefix.expireSeconds();
            if(expireSecond<=0)
                jedis.set(realKey,str);
            else
                jedis.setex(realKey,expireSecond,str);
            return true;
        }finally {
            returnToResource(jedis);
        }
    }

    /*
        将bean转换为字符串
     */
    public static  <T> String beanToString(T value) {
        if(value==null)
            return null;
        Class<?> clazz = value.getClass();
        if(clazz==int.class||clazz==Integer.class||clazz==long.class||clazz==Long.class)
            return ""+value;
        else if(clazz==String.class)
            return (String) value;
        else
            return JSON.toJSONString(value);
    }

    /**
     * 将String字符串转换为需要的Bean
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static  <T> T stringToBean(String str, Class<T> clazz) {
        if(str == null || str.length()<=0|| clazz == null)
            return null;
        else if(clazz == Integer.class|| clazz == int.class)
            return (T) Integer.valueOf(str);
        else if( clazz == String.class)
            return (T) str;
        else if(clazz==long.class||clazz==Long.class)
            return (T)Long.valueOf(str);
        else
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
    }

    /*
            将连接资源返回给资源池
     */
    private void returnToResource(Jedis jedis) {
        if(jedis!=null)
            jedis.close();
    }

    /*
        判断指定key的对象是否存在
     */
    public <T> boolean exits(KeyPrefix keyPrefix , String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix()+key;
            if(jedis.exists(realKey))
                return true;
            else
                return false;
        }finally {
            returnToResource(jedis);
        }
    }

    /*
        增加值
     */
    public <T> Long incr(KeyPrefix keyPrefix,String key){
        Jedis jedis= null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix()+key;
            Long value =  jedis.incr(realKey);
            return value;
        }finally {
            returnToResource(jedis);
        }
    }

    /*
        减少值
     */
    public <T> Long decr(KeyPrefix keyPrefix,String key){
        Jedis jedis= null;
        try {
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix()+key;
            Long value =  jedis.decr(realKey);
            return value;
        }finally {
            returnToResource(jedis);
        }
    }
}
