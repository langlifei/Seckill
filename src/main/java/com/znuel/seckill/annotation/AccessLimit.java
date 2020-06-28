package com.znuel.seckill.annotation;

import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/28 19:13
 * @Describe
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    int seconds();//时间限制
    int maxCount();//次数限制
    boolean needLogin() default true;
}
