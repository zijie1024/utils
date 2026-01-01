package com.zijie1024.common.utils.mq.rabbit.example;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @author 字节幺零二四
 * @date 2024-04-12 15:40
 * @description Receiver
 */
@Service
@RequiredArgsConstructor
public class Receiver {

    /**
     * 接收普通消息
     *
     * @param data    消息主体
     * @param msg     消息
     * @param channel 通道
     */
    @SneakyThrows
    @RabbitListener(queues = Constant.QUEUE)
    public void receiveMsg(String data, Message msg, Channel channel) {
        try {
            // 此处可通过对消息主体data进行判断来决定是否执行任务
            System.out.println(data);
        } catch (Exception e) {
            // 写日志，通知维护人员
            System.out.println(e);
        }
        // 确认消息已被消费
        // 无论是否成功处理，此消息确已被消费者成功接收
        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 接收延时消息
     *
     * @param data    消息主体
     * @param msg     消息
     * @param channel 通道
     */
    @SneakyThrows
    @RabbitListener(queues = Constant.DELAY_MSG_QUEUE)
    public void receiveDelayMsg(String data, Message msg, Channel channel) {
        MessageProperties ps = msg.getMessageProperties();
        try {
            // 获取附加参数
            String name = ps.getHeader(Constant.ARGS_NAME);
            System.out.println(name);
            // 此处可通过对消息主体data进行判断来决定是否执行任务
            System.out.println(data);
        } catch (Exception e) {
            // 写日志，通知维护人员
            System.out.println(e);
        }
        // 确认消息已被消费
        // 无论是否成功处理，此消息确已被消费者成功接收
        channel.basicAck(ps.getDeliveryTag(), false);
    }
}
