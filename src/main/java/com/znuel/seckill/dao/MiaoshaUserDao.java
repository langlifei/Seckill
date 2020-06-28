package com.znuel.seckill.dao;

import com.znuel.seckill.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 17:27
 * @Describe
 */
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id = #{id}")
    public MiaoshaUser getById(Long id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    int updatePass(MiaoshaUser toBeUpdate);
}
