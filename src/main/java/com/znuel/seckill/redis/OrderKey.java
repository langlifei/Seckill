package com.znuel.seckill.redis;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/26 11:40
 * @Describe
 */

public class OrderKey extends BasePrefix {
    private OrderKey(String prefix) {
        super(prefix);
    }

    private OrderKey(String prefix, int expireSecond) {
        super(prefix, expireSecond);
    }

    public final static OrderKey getMiaoshaOrderByUidGid = new OrderKey("miaoshaUinAndGid");
}
