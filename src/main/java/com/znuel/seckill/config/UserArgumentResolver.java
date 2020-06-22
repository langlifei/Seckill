package com.znuel.seckill.config;

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
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/22 16:25
 * @Describe
 */

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private MiaoshaUserService miaoshaUserService;

    //如果supportsParameter通过，才会执行resolveArgument
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz== MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request =  nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response =  nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        String paramToken = request.getParameter(MiaoshaUserServiceImp.COOKIE_TOKEN);
        String cookieToken = getCookieValue(request);
        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken))
            return "login";
        String token = !StringUtils.isEmpty(paramToken)?paramToken:cookieToken;//cookieToken优先级低一点
        MiaoshaUser miaoshaUser = miaoshaUserService.getByToken(response,token);
        return miaoshaUser;
    }

    private String getCookieValue(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies)
            if(cookie.getName().equals(MiaoshaUserServiceImp.COOKIE_TOKEN))
                return cookie.getValue();
        return null;
    }
}
