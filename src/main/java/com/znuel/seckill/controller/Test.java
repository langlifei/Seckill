package com.znuel.seckill.controller;

import com.znuel.seckill.redis.RedisService;
import com.znuel.seckill.redis.UserKey;
import com.znuel.seckill.result.CodeMsg;
import com.znuel.seckill.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/20 13:55
 * @Describe
 */
@RestController
public class Test {

    @Autowired
    RedisService redisService;

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/redis/set")
    public Result<String> redisTest(){
        redisService.set(UserKey.getByID,"1","Hello Redis");
        String value = redisService.get(UserKey.getByID,"1",String.class);
        return Result.errorCode(CodeMsg.SERVER_ERROR);
    }
}
