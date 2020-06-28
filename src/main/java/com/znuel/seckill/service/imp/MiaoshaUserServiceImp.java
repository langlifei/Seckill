package com.znuel.seckill.service.imp;

import com.znuel.seckill.dao.MiaoshaUserDao;
import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.exception.GlobalException;
import com.znuel.seckill.redis.MiaoshaUserkey;
import com.znuel.seckill.redis.RedisService;
import com.znuel.seckill.result.CodeMsg;
import com.znuel.seckill.service.MiaoshaUserService;
import com.znuel.seckill.util.MD5Util;
import com.znuel.seckill.util.UUIDUtil;
import com.znuel.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 17:25
 * @Describe
 */

@Service
public class MiaoshaUserServiceImp implements MiaoshaUserService {

    public final static String COOKIE_TOKEN = "token";

    @Autowired(required = false)
    private MiaoshaUserDao miaoshaUserDao;
    @Autowired
    private RedisService redisService;

    public MiaoshaUser getById(Long id){
        MiaoshaUser user  = redisService.get(MiaoshaUserkey.GETBYID,""+id,MiaoshaUser.class);
        if(user!=null)
            return user;
        user = miaoshaUserDao.getById(id);
        if(user!=null)
            redisService.set(MiaoshaUserkey.GETBYID,""+id,user);
        return user;
    }

    public boolean updatePassword(long id,String token,String newPassword){
        //通常只修改需要修改的列,不要修改整个对象,避免耗费不必要的性能
        MiaoshaUser user = getById(id);
        //如果用户不存在，抛出全局异常
        if(user==null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXITS);
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.fromPassToDB(newPassword,user.getSalt()));
        user.setPassword(toBeUpdate.getPassword());
        //更新数据库
        miaoshaUserDao.updatePass(toBeUpdate);
        //修改用户缓存数据(可通过MiaoshaUserKey查看缓存了哪些数据)
        redisService.set(MiaoshaUserkey.GETBYID,""+id,user);
        redisService.set(MiaoshaUserkey.TOKEN,token,user);
        return true;
    }

    @Override
    public boolean login(HttpServletResponse response,LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if(miaoshaUser==null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXITS);
        //验证密码
        String dbPass = miaoshaUser.getPassword();
        String saltDB = miaoshaUser.getSalt();
        String calcPass = MD5Util.fromPassToDB(password,saltDB);
        if(!dbPass.equals(calcPass))
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        String token = UUIDUtil.uuid();
        addCookieToken(response,token,miaoshaUser);
        return true;
    }

    public void addCookieToken(HttpServletResponse response,String token,MiaoshaUser miaoshaUser){
        Cookie cookie = new Cookie(COOKIE_TOKEN,token);
        redisService.set(MiaoshaUserkey.TOKEN,token,miaoshaUser);
        cookie.setMaxAge(MiaoshaUserkey.TOKEN.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Override
    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token))
            return null;
       MiaoshaUser user =  redisService.get(MiaoshaUserkey.TOKEN,token,MiaoshaUser.class);
       if(user!=null)
           addCookieToken(response,token,user);
       return user;
    }
}
