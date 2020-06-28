package com.znuel.seckill.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Zeng Zhuo
 * @Date 2020/6/26 21:34
 * @Describe
 */

@Configuration
public class MQConfig {
    public static final String QUEUE = "queue";

    @Bean
    public Queue queue(){
        //注意不要导错包
        return new Queue(QUEUE,true);
    }
}
