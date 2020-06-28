package com.znuel.seckill.vo;

import com.znuel.seckill.domain.MiaoshaUser;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/25 19:07
 * @Describe
 */

public class DetailVo {

    private MiaoshaUser miaoshaUser;
    private GoodsVo goodsVo;
    private int miaoshaStatus;
    private int remainSeconds;

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }
}
