package com.znuel.seckill.controller;

import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.redis.GoodsKey;
import com.znuel.seckill.redis.RedisService;
import com.znuel.seckill.result.Result;
import com.znuel.seckill.service.imp.GoodsService;
import com.znuel.seckill.vo.DetailVo;
import com.znuel.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
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
    private GoodsService goodsService;
    @Autowired
    RedisService redisService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;


    /*
    * 目的：防止大量访问突然访问某个页面,造成数据库的访问量过大，给数据库造成巨大的压力，使用页面缓存可以缓解数据库的压力
    *      注：由于数据可能发生变化，故缓存的时间不能过长，此处设置为一分钟
    * produces:表明直接返回html的源代码
    * */
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String list(HttpServletRequest request,HttpServletResponse response,Model model, MiaoshaUser user) {
        model.addAttribute("user", user);
//        return "goods_list";
        //获取页面缓存
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        //如果缓存中有数据，直接返回。
        if(!StringUtils.isEmpty(html))
            return html;
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        //如果缓存中不包含数据，手动渲染后将数据加入缓存，并返回数据。
        //手动渲染
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",webContext);
        //如果渲染后，数据不为空，则存入缓存
        if(!StringUtils.isEmpty(html))
            redisService.set(GoodsKey.getGoodsList,"",html);
        return html;
    }


    /*
        对不同的商品做url缓存
     */
    @RequestMapping(value = "/to_detail2/{goodsId}" , produces = "text/html")
    @ResponseBody
    public String detail2(HttpServletRequest request,HttpServletResponse response,
                         Model model , MiaoshaUser miaoshaUser, @PathVariable("goodsId") long goodsId){
        model.addAttribute("user",miaoshaUser);
        //获取url缓存
        String html = redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
        //如果缓存中有数据，直接返回。
        if(!StringUtils.isEmpty(html))
            return html;
        //从数据库获取信息，手动渲染数据，将页面存入缓存
        //通常ID在分布式场景下使用snowflake算法来实现分布式ID;
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goodsVo);
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
        //如果缓存中不包含数据，手动渲染后将数据加入缓存，并返回数据。
        //手动渲染
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",webContext);//第一个参数是模板命名，第二个是context
        //如果渲染后，数据不为空，则存入缓存
        if(!StringUtils.isEmpty(html))
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        return html;
    }

    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<DetailVo> detail(MiaoshaUser miaoshaUser, @PathVariable("goodsId") long goodsId){
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
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
        DetailVo detailVo = new DetailVo();
        detailVo.setGoodsVo(goodsVo);
        detailVo.setMiaoshaStatus(miaoshaStatus);
        detailVo.setRemainSeconds(remainSeconds);
        detailVo.setMiaoshaUser(miaoshaUser);
        return Result.success(detailVo);
    }
}
