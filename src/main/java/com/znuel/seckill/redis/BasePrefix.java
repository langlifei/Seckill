package com.znuel.seckill.redis;

import java.security.Key;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 12:39
 * @Describe
 */

public abstract class BasePrefix implements KeyPrefix {

    private String prefix;
    private int expireSecond;

    public BasePrefix(String prefix) {
        this(prefix,0);
    }

    public BasePrefix(String prefix, int expireSecond) {
        this.prefix = prefix;
        this.expireSecond = expireSecond;
    }

    @Override
    public int expireSeconds() {
        return this.expireSecond;
    }

    @Override
    public String getPrefix() {
        //这里的getClass获取的是子类的类对象
        String className = getClass().getSimpleName();
        return className+":"+prefix+":";
    }

}
