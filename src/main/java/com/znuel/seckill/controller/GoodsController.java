package com.znuel.seckill.controller;

import com.znuel.seckill.dao.GoodsDao;
import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.service.MiaoshaUserService;
import com.znuel.seckill.service.imp.GoodsService;
import com.znuel.seckill.service.imp.MiaoshaUserServiceImp;
import com.znuel.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 11:25
 * @Describe
 */
@RequestMapping("/goods")
@Controller
public class GoodsController {

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private GoodsService goodsService;


    @RequestMapping("/to_list")
    public String list(Model model,MiaoshaUser user) {
        model.addAttribute("user", user);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String detail(Model model , MiaoshaUser miaoshaUser, @PathVariable("goodsId") long goodsId){
        //通常ID在分布式场景下使用snowflake算法来实现分布式ID;
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goodsVo);
        model.addAttribute("user",miaoshaUser);
        //秒杀时间 (如果是分布式的，各个时间不一致应该统一从一台机器上获取时间)
        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if(now<startAt){
            miaoshaStatus = 0;//秒杀还未开始,倒计时
            remainSeconds = (int)((startAt-now)/1000);
        }else if(now>endAt){
            miaoshaStatus = 2;//秒杀已经结束。
            remainSeconds = -1;
        }else{
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        return "goods_detail";
    }
}
