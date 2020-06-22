package com.znuel.seckill.util;

import com.alibaba.druid.support.spring.stat.annotation.Stat;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 16:10
 * @Describe
 */

public class MD5Util {

    private static final String SALT = "1Q2W3E4R5T6Y7U8I9O0P";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    /*
        对用户输入的密码进行加密，使得用户密码在网络中使用密文传输。
     */
    public static String inputPassFromPass(String inputPass){
        //char要转换为字符串,否则两个char相加会出现ASCLL码相加的数值。
        String str = ""+SALT.charAt(0)+SALT.charAt(3)+SALT.charAt(5)+inputPass+SALT.charAt(7)+SALT.charAt(9)+SALT.charAt(6);
        return md5(str);
    }

    /**
        对用户输入的密码再次进行MD5加密,将其存入数据库，防止通过反查表进行推出密码。
     */

    public static String fromPassToDB(String inputPass,String salt){
        String str = ""+salt.charAt(0)+salt.charAt(3)+salt.charAt(5)+inputPass+salt.charAt(7)+salt.charAt(9)+salt.charAt(6);
        return md5(str);
    }

}
