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

    public void send(Object message){
        String str = RedisService.beanToString(message);
        System.out.println("sender:" + str);
        amqpTemplate.convertAndSend(MQConfig.QUEUE,str);
    }
}
