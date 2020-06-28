package com.znuel.seckill.rabbitmq;

import com.znuel.seckill.redis.RedisService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/26 20:56
 * @Describe
 */

@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    public void miaoshaSend(Object message){
        String str = RedisService.beanToString(message);
        amqpTemplate.convertAndSend(MQConfig.QUEUE,str);
    }
}
