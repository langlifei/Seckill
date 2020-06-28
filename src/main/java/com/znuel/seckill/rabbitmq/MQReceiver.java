package com.znuel.seckill.rabbitmq;

import com.znuel.seckill.domain.MiaoshaOrder;
import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.domain.OrderInfo;
import com.znuel.seckill.redis.RedisService;
import com.znuel.seckill.result.CodeMsg;
import com.znuel.seckill.result.Result;
import com.znuel.seckill.service.MiaoshaService;
import com.znuel.seckill.service.OrderService;
import com.znuel.seckill.service.imp.GoodsService;
import com.znuel.seckill.vo.GoodsVo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/26 20:56
 * @Describe
 */
@Service
public class MQReceiver {

    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){
        MiaoshaMessage miaoshaMessage = RedisService.stringToBean(message,MiaoshaMessage.class);
        Long goodsId = miaoshaMessage.getGoodsId();
        MiaoshaUser miaoshaUser = miaoshaMessage.getMiaoshaUser();
        //可能有多条相同的请求进入了队列，故需在队列中再次防止重复秒杀
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(),goodsId);
        if(miaoshaOrder!=null)
            return ;
        //访问数据库，查看是否存在库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        long stock = goodsVo.getStockCount();
        if(stock<=0)
            return;
        //减库存->写入订单表->写入秒杀订单表
        miaoshaService.miaosha(miaoshaUser,goodsVo);
    }
}
