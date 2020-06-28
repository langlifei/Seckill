package com.znuel.seckill.vo;

import com.znuel.seckill.domain.OrderInfo;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/26 0:11
 * @Describe
 */

public class OrderDetailVo {
    private GoodsVo goods;
    private OrderInfo order;
    public GoodsVo getGoods() {
        return goods;
    }
    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }
    public OrderInfo getOrder() {
        return order;
    }
    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}
