package com.znuel.seckill.util;

import java.util.UUID;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 11:09
 * @Describe
 */

public class UUIDUtil {

    public static String uuid(){
       return UUID.randomUUID().toString().replace("-","");
    }
}
