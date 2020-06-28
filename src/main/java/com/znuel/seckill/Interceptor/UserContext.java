package com.znuel.seckill.Interceptor;

import com.znuel.seckill.domain.MiaoshaUser;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/28 19:27
 * @Describe
 */

public class UserContext {
    private static ThreadLocal<MiaoshaUser> userInfo = new ThreadLocal<>();

    public static MiaoshaUser getUser(){
        return userInfo.get();
    }

    public static void setUser(MiaoshaUser miaoshaUser){
        userInfo.set(miaoshaUser);
    }
}
