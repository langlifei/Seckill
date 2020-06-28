package com.znuel.seckill.service;

import com.znuel.seckill.dao.OrderDao;
import com.znuel.seckill.domain.MiaoshaOrder;
import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.domain.OrderInfo;
import com.znuel.seckill.redis.OrderKey;
import com.znuel.seckill.redis.RedisService;
import com.znuel.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 22:42
 * @Describe
 */
@Service
public class OrderService {
    @Autowired(required = false)
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long id, long goodsId) {
        MiaoshaOrder miaoshaOrder = redisService.get(OrderKey.getMiaoshaOrderByUidGid,""+id+"_"+goodsId,MiaoshaOrder.class);
        if(miaoshaOrder==null)
            miaoshaOrder = orderDao.getMiaoshaOrderByUserIdGoodsId(id,goodsId);
        return miaoshaOrder;
    }

    public OrderInfo createOrder(MiaoshaUser miaoshaUser, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(miaoshaUser.getId());
        orderDao.insert(orderInfo);
        return orderInfo;
    }

    public boolean createMiaoshaOrder(OrderInfo orderInfo) {
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(orderInfo.getGoodsId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(orderInfo.getUserId());
        if(orderDao.insertMiaoshaOrder(miaoshaOrder)>0){
            redisService.set(OrderKey.getMiaoshaOrderByUidGid,""+miaoshaOrder.getUserId()+"_"+miaoshaOrder.getGoodsId(),miaoshaOrder);
            return true;
        }
        else
            return false;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
