package com.znuel.seckill.validator;

import com.znuel.seckill.util.ValidatorUtil;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 9:07
 * @Describe
 */

/*
        实现ConstraintValidator接口的两个参数：
        1.验证的注解
        2.注解验证的类型
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {
    private boolean required;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required) {
            return ValidatorUtil.isMobile(s);
        }else{
            if(StringUtils.isEmpty(s))
                return true;
            else
                return ValidatorUtil.isMobile(s);
        }
    }
}
