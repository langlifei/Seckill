package com.znuel.seckill.service;

import com.znuel.seckill.domain.MiaoshaGoods;
import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.domain.OrderInfo;
import com.znuel.seckill.service.imp.GoodsService;
import com.znuel.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 22:45
 * @Describe
 */
@Service
public class MiaoshaService {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goodsVo) {
        //减库存
        goodsService.reduceStock(goodsVo);
        //创建商品订单
        OrderInfo orderInfo = orderService.createOrder(miaoshaUser,goodsVo);
        //创建秒杀商品订单
        orderService.createMiaoshaOrder(orderInfo);
        return orderInfo;
    }
}
