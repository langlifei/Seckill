package com.znuel.seckill.redis;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/21 12:36
 * @Describe 定义一个key前缀接口，防止key名重复。
 *           借助模板模式思想。接口---->抽象类----->实现类。接口定义协议，抽象类完成共有方法，实现类实现特有行为。
 */

public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();

}
