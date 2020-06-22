package com.znuel.seckill.redis;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 12:47
 * @Describe
 */

public class UserKey extends BasePrefix{

    private UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey getByID = new UserKey("id");
    public static UserKey getByName = new UserKey("name");

}
