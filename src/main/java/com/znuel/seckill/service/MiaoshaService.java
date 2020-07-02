package com.znuel.seckill.service;

import com.znuel.seckill.domain.MiaoshaOrder;
import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.domain.OrderInfo;
import com.znuel.seckill.exception.GlobalException;
import com.znuel.seckill.redis.GoodsKey;
import com.znuel.seckill.redis.MiaoshaKey;
import com.znuel.seckill.redis.RedisService;
import com.znuel.seckill.result.CodeMsg;
import com.znuel.seckill.service.imp.GoodsService;
import com.znuel.seckill.util.MD5Util;
import com.znuel.seckill.util.UUIDUtil;
import com.znuel.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

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
    @Autowired
    private RedisService redisService;

    private static final char[] VERIFY_OPERATION = new char[]{'+','-','*'};

    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goodsVo) {
        //减库存
        boolean isReduce = goodsService.reduceStock(goodsVo);
        //减库存失败，直接返回。
        if(!isReduce){
            //通过标记来区分是卖完，还是正在排队中
            setGoodsOver(goodsVo.getId());
            return null;
        }
        //创建商品订单
        OrderInfo orderInfo = orderService.createOrder(miaoshaUser,goodsVo);
        //创建秒杀商品订单
        orderService.createMiaoshaOrder(orderInfo);
        return orderInfo;
    }


    public long getMiaoshaResult(Long userId,long goodsId) {
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if(miaoshaOrder!=null){//如果订单信息存在,则代表秒杀成功
            return miaoshaOrder.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            return isOver?-1:0;
        }
    }


    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodOver,""+goodsId,true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exits(MiaoshaKey.isGoodOver,""+goodsId);
    }

    public boolean checkPath(MiaoshaUser miaoshaUser, long goodsId, String path) {
        String oldPath = redisService.get(MiaoshaKey.getMiaoshaPath,""+miaoshaUser.getId()+"_"+goodsId,String.class);
        return oldPath.equals(path);
    }

    public String createMiaoshaPath(MiaoshaUser miaoshaUser, Long goodsId) {
        String path = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath,""+miaoshaUser.getId()+"_"+goodsId,path);
        return path;
    }

    public BufferedImage createVerifyCode(MiaoshaUser miaoshaUser,long goodsId) {
        if(miaoshaUser==null||goodsId<0)
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode+"=", 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, miaoshaUser.getId()+"_"+goodsId, rnd);
        //输出图片
        return image;
    }

    private int calc(String verifyCode) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(verifyCode);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String generateVerifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char op1 = VERIFY_OPERATION[random.nextInt(3)];//生成操作符
        char op2 = VERIFY_OPERATION[random.nextInt(3)];
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(num1);
        stringBuilder.append(op1);
        stringBuilder.append(num2);
        stringBuilder.append(op2);
        stringBuilder.append(num3);
        return stringBuilder.toString();
    }

    public boolean checkVerifyCode(MiaoshaUser miaoshaUser, Long goodsId, Integer verifyCode) {
       Integer result =  redisService.get(MiaoshaKey.getMiaoshaVerifyCode, miaoshaUser.getId()+"_"+goodsId, Integer.class);
        if(result != null && verifyCode-result==0){
            redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, miaoshaUser.getId()+"_"+goodsId);
            return true;
        }
        else
            return false;
    }
}
