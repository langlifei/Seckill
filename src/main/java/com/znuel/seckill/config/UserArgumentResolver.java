package com.znuel.seckill.config;

import com.znuel.seckill.Interceptor.UserContext;
import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.service.MiaoshaUserService;
import com.znuel.seckill.service.imp.MiaoshaUserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 16:25
 * @Describe
 */

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    //如果supportsParameter通过，才会执行resolveArgument
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz== MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return UserContext.getUser();
    }
}
