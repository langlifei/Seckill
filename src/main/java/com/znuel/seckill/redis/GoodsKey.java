package com.znuel.seckill.redis;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/25 15:44
 * @Describe
 */

public class GoodsKey extends BasePrefix {
    public GoodsKey(String prefix) {
        super(prefix);
    }

    public GoodsKey(String prefix, int expireSecond) {
        super(prefix, expireSecond);
    }

    public static GoodsKey getGoodsList = new GoodsKey("goodsList",60);
    public static GoodsKey getGoodsDetail = new GoodsKey("goodsDetail",60);
}
