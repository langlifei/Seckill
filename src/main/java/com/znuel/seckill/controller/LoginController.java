package com.znuel.seckill.controller;

import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.rabbitmq.MQSender;
import com.znuel.seckill.result.CodeMsg;
import com.znuel.seckill.result.Result;
import com.znuel.seckill.service.MiaoshaUserService;
import com.znuel.seckill.util.ValidatorUtil;
import com.znuel.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 17:01
 * @Describe
 */

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    MQSender sender;


    @RequestMapping("/to_login")
    public String to_login(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){
        // 执行登录
        miaoshaUserService.login(response,loginVo);
        return Result.success(true);
    }

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> test(){
        sender.send("Hello RabbitMQ");
        return Result.success("Hello world");
    }
}
