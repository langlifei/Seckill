package com.znuel.seckill.service;

import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 17:32
 * @Describe
 */

public interface MiaoshaUserService {

    public MiaoshaUser getById(Long id);

    public boolean login(HttpServletResponse response,LoginVo loginVo);

    MiaoshaUser getByToken(HttpServletResponse response,String token);
}
