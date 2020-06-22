package com.znuel.seckill.util;

import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 18:00
 * @Describe
 */

public class ValidatorUtil {

    public static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String src){
        if(StringUtils.isEmpty(src))
            return false;
        Matcher matcher = MOBILE_PATTERN.matcher(src);
        return matcher.matches();
    }
}
