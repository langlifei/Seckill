package com.znuel.seckill.controller;

import com.znuel.seckill.annotation.AccessLimit;
import com.znuel.seckill.domain.MiaoshaOrder;
import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.domain.OrderInfo;
import com.znuel.seckill.rabbitmq.MQSender;
import com.znuel.seckill.rabbitmq.MiaoshaMessage;
import com.znuel.seckill.redis.GoodsKey;
import com.znuel.seckill.redis.RedisService;
import com.znuel.seckill.result.CodeMsg;
import com.znuel.seckill.result.Result;
import com.znuel.seckill.service.MiaoshaService;
import com.znuel.seckill.service.OrderService;
import com.znuel.seckill.service.imp.GoodsService;
import com.znuel.seckill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 21:53
 * @Describe
 */

@RequestMapping("/miaosha")
@Controller
public class MiaoshaController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private MQSender sender;
    @Autowired
    private RedisService redisService;

    private Map<String,Boolean> isOver = new HashMap<>(); //用来标志商品是否还有库存
    /*
        容器启动时进行调用...
        将秒杀商品库存信息加入到缓存中
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list =  goodsService.listGoodsVo();
        if(list==null)
            return ;
        //容器启动时将商品库存信息加入到redis中
        for (GoodsVo goods:list){
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goods.getId(),goods.getStockCount());
            isOver.put(""+goods.getId(),false);//初试化商品库存状态
        }

    }


    /*
        增加了rabbitmq后的秒杀
     */
    @PostMapping("/{path}/do_miaosha")
    @ResponseBody
    public Result<Integer> do_miaosha(MiaoshaUser miaoshaUser,
                                        @RequestParam("goodsId")long goodsId,@PathVariable("path")String path){
        if(miaoshaUser==null)
            return Result.error(CodeMsg.SERVER_ERROR);
        boolean check = miaoshaService.checkPath(miaoshaUser,goodsId,path);
        if(!check)
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        if(isOver.get(""+goodsId)==null || isOver.get(""+goodsId))
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        //读取redis中的库存信息
        long stock = redisService.get(GoodsKey.getMiaoshaGoodsStock,""+goodsId,long.class);
        //如果秒杀结束，则更改map中商品的标记，之后的请求则可不必访问redis
        if(stock<=0){
            isOver.put(""+goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否秒杀过，防止重复秒杀
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(),goodsId);
        if(miaoshaOrder!=null)
            return Result.error(CodeMsg.REPEAT_MIAO_SHA);
        //将请求加入消息队列
        MiaoshaMessage message = new MiaoshaMessage();
        message.setMiaoshaUser(miaoshaUser);
        message.setGoodsId(goodsId);
        sender.miaoshaSend(message);
        return Result.success(0);//排队中
    }

    @PostMapping("/do_miaosha1")
    @ResponseBody
    public Result<OrderInfo> do_miaosha1(MiaoshaUser miaoshaUser,
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

    /**
        OrderId:成功
        -1：失败
        0：排队中
     */
    @GetMapping("/result")
    @ResponseBody
    public Result<Long> orderResult(MiaoshaUser miaoshaUser,
                                    @RequestParam("goodsId") Long goodsId){
        if(miaoshaUser==null||goodsId<0)
            return Result.error(CodeMsg.SERVER_ERROR);
        long result = miaoshaService.getMiaoshaResult(miaoshaUser.getId(),goodsId);
        return Result.success(result);
    }

    @GetMapping("/path")
    @ResponseBody
    @AccessLimit(seconds = 10,maxCount = 5)
    public Result<String> getMiaoshaPath(MiaoshaUser miaoshaUser,@RequestParam("goodsId") Long goodsId,
                                         @RequestParam("verifyCode")Integer verifyCode){
        if(miaoshaUser==null)
            return Result.error(CodeMsg.SERVER_ERROR);
        boolean check = miaoshaService.checkVerifyCode(miaoshaUser,goodsId,verifyCode);
        if(!check)
            return Result.error(CodeMsg.VERIFYCODE_ERROR);
        String path = miaoshaService.createMiaoshaPath(miaoshaUser,goodsId);
        return Result.success(path);
    }

    @GetMapping("/verifyCode")
    @ResponseBody
    public Result<String> verifyCode(HttpServletResponse response,MiaoshaUser miaoshaUser, @RequestParam("goodsId") Long goodsId){
        if(miaoshaUser==null||goodsId<0)
            return Result.error(CodeMsg.SERVER_ERROR);
        try {
            BufferedImage image  = miaoshaService.createVerifyCode(miaoshaUser, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}
