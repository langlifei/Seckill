package com.znuel.seckill.rabbitmq;

import com.znuel.seckill.domain.MiaoshaUser;
import com.znuel.seckill.vo.GoodsVo;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/28 11:13
 * @Describe
 */

public class MiaoshaMessage {
    private MiaoshaUser miaoshaUser;
    private Long goodsId;

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
