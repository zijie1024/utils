package com.zijie1024.common.utils.mq.rabbit.example;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author 字节幺零二四
 * @date 2024-04-12 16:09
 * @description 注册交换机和队列
 */

@Configuration
public class Config {


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(Constant.EXCHANGE, true, false);
    }

    @Bean
    public Queue queue() {
        return new Queue(Constant.QUEUE, true);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(Constant.ROUTING);
    }

    @Bean
    public CustomExchange delayExchange() {
        HashMap<String, Object> param = new HashMap<>() {{
            put("x-delayed-type", "direct");
        }};
        return new CustomExchange(
                Constant.EXCHANGE,
                "x-delayed-message",
                true,
                false,
                param
        );
    }

    @Bean
    public Queue delayQUeue() {
        return new Queue(Constant.DELAY_MSG_QUEUE, true);
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder
                .bind(delayQUeue())
                .to(delayExchange())
                .with(Constant.DELAY_MSG_ROUTING)
                .noargs();
    }


}
