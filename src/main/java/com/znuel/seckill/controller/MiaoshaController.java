package com.znuel.seckill.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 21:53
 * @Describe
 */

@RequestMapping("/miaosha")
@Controller
public class MiaoshaController{

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;

    @PostMapping("/do_miaosha")
    @ResponseBody
    public Result<OrderInfo> do_miaosha(Model model , MiaoshaUser miaoshaUser,
                             @RequestParam("goodsId")long goodsId){
        if(miaoshaUser==null)
            return Result.error(CodeMsg.SERVER_ERROR);
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        //判断库存是否足够
        int stock = goodsVo.getGoodsStock();
        //如果库存不够，则防止继续秒杀
        if(stock<=0)
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        //查看是否刚刚下过订单
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(),goodsId);
        if(miaoshaOrder!=null)
            return Result.error(CodeMsg.REPEAT_MIAO_SHA);
        //正常进入秒杀
        //减库存->写入订单表->写入秒杀订单表
        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser,goodsVo);
        return Result.success(orderInfo);
    }
}
