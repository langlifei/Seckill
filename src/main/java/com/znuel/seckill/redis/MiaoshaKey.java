package com.znuel.seckill.redis;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/28 12:27
 * @Describe
 */

public class MiaoshaKey extends BasePrefix {


    public MiaoshaKey(String prefix) {
        super(prefix);
    }

    public MiaoshaKey(String prefix, int expireSecond) {
        super(prefix, expireSecond);
    }
    public static final MiaoshaKey isGoodOver = new MiaoshaKey("goodsOver");
    public static final MiaoshaKey getMiaoshaPath = new MiaoshaKey("miaoshaPath",60);
    public static final MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey("verifyCode",3*60);
    public static final MiaoshaKey isRepeatRequest = new MiaoshaKey("repeatRequest",3);
    public static MiaoshaKey overClick(int expireSecond){
        return new MiaoshaKey("overClick",expireSecond);
    }
}
