package com.znuel.seckill.controller;

import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.service.MiaoshaUserService;
import com.znuel.seckill.service.imp.MiaoshaUserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;

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

    @RequestMapping("/to_list")
    public String to_list(Model model,MiaoshaUser miaoshaUser){
        model.addAttribute("user",miaoshaUser);
        return "goods_list";
    }

}
