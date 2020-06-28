package com.znuel.seckill.Interceptor;

import com.znuel.seckill.annotation.AccessLimit;
import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.exception.GlobalException;
import com.znuel.seckill.redis.MiaoshaKey;
import com.znuel.seckill.redis.RedisService;
import com.znuel.seckill.result.CodeMsg;
import com.znuel.seckill.service.MiaoshaUserService;
import com.znuel.seckill.service.imp.MiaoshaUserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/28 19:19
 * @Describe  先执行拦截器，后执行参数解析，故将用户认证转移到此处
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            if(UserContext.getUser()==null)
                UserContext.setUser(getUser(request,response));
            MiaoshaUser miaoshaUser = UserContext.getUser();
            AccessLimit accessLimit = ((HandlerMethod) handler).getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null)
                return true;
            if(accessLimit.needLogin()){
                if(miaoshaUser==null)
                    throw new GlobalException(CodeMsg.USER_NOT_LOGIN);
            }
            if(isOverClick(accessLimit,request.getRequestURI(),miaoshaUser.getId()))
                throw new GlobalException(CodeMsg.REQUEST_OVER_FREQUENCE);
            else
                return true;
        }else
            return true;
    }

    private MiaoshaUser getUser(HttpServletRequest request,HttpServletResponse response){
        String paramToken = request.getParameter(MiaoshaUserServiceImp.COOKIE_TOKEN);
        String cookieToken = getCookieValue(request);
        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken))
            return null;
        String token = !StringUtils.isEmpty(paramToken)?paramToken:cookieToken;//cookieToken优先级低一点
        MiaoshaUser miaoshaUser = miaoshaUserService.getByToken(response,token);
        return miaoshaUser;
    }

    private String getCookieValue(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies==null||cookies.length<=0)
            return null;
        for (Cookie cookie:cookies)
            if(cookie.getName().equals(MiaoshaUserServiceImp.COOKIE_TOKEN))
                return cookie.getValue();
        return null;
    }

    private boolean isOverClick(AccessLimit accessLimit,String url,long userId){
        int seconds = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();
        Integer count = redisService.get(MiaoshaKey.overClick(seconds), url + "_" + userId, Integer.class);
        if(count==null)
            redisService.set(MiaoshaKey.overClick(seconds), url + "_" + userId, 1);
        else if(count<maxCount)
            redisService.incr(MiaoshaKey.overClick(seconds),url + "_" + userId);
        else
            return true;
        return false;
    }
}
