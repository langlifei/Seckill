package com.znuel.seckill.redis;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 11:16
 * @Describe
 */

public class MiaoshaUserkey extends BasePrefix{

    private static final int TOKEN_EXPIRE_TIME = 24*60*60*2;

    public MiaoshaUserkey(String prefix) {
        super(prefix);
    }

    public MiaoshaUserkey(String prefix, int expireSecond) {
        super(prefix, expireSecond);
    }

    public final static MiaoshaUserkey TOKEN = new MiaoshaUserkey("token",TOKEN_EXPIRE_TIME);
}
