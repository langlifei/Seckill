package com.znuel.seckill.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/26 20:56
 * @Describe
 */
@Service
public class MQReceiver {

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){
        System.out.println("receiver:"+message);
    }
}
