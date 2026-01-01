package com.zijie1024.common.utils.mq.rabbit;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 字节幺零二四
 * @date 2025-03-01 16:52
 * @description RabbitMqManager
 */
@Component
@RequiredArgsConstructor
public class IRabbitManager {

    private final IRabbitProperties properties;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 发送消息
     *
     * @param msg        消息
     * @param businessId 业务ID
     * @param exchange   交换机
     * @param routingKey 路由
     * @param delayTime  延迟时间，单位：秒，null表示不延迟
     * @param retryCount 可重试次数
     * @param args       附加参数
     */
    public void sendMsg(
            Object msg,
            Object businessId,
            String exchange,
            String routingKey,
            long delayTime,
            int retryCount,
            Map<String, String> args
    ) {
        // 封装消息
        boolean isDelay = delayTime > 0;
        args = new HashMap<>(args);
        args.put(properties.getHeader().getDelayFlagKey(), String.valueOf(isDelay));
        ICorrelationData data = new ICorrelationData(
                msg, exchange, routingKey, retryCount, delayTime, args
        );
        // 缓存消息
        IRabbitHelper.setMsgCache(data.getId(), data);
        // 发送消息
        Map<String, String> as = args;
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                msg,
                m -> {
                    MessageProperties mp = m.getMessageProperties();
                    if (isDelay) mp.setDelayLong(TimeUnit.SECONDS.toMillis(delayTime));
                    if (businessId != null) {
                        mp.setHeader(properties.getHeader().getBusinessIdKey(), businessId);
                        IRabbitHelper.setCancelFlag(businessId, data.getId(), false);
                    }
                    as.forEach(mp::setHeader);
                    return m;
                },
                data
        );
    }

    public void sendMsg(Object msg, Object businessId, String exchange, String routingKey) {
        sendMsg(msg, businessId, exchange, routingKey, 0, properties.getRetryCount(), new HashMap<>());
    }

    /**
     * 取消消息
     * 使用者发送消息时需要设置ID
     *
     * @param id 消息ID
     */
    public void cancelMsg(Object id) {
        IRabbitHelper.cancelMsg(id);
    }

    /**
     * 消费消息
     * 执行使用者消费消息的方法，并对单条消息进行确认
     *
     * @param msg               消息
     * @param channel           通道
     * @param doBusinessConsume 业务逻辑
     */
    public void consume(Message msg, Channel channel, Runnable doBusinessConsume) throws IOException {
        if (!IRabbitHelper.isCanceled(msg)) {
            doBusinessConsume.run();
        }
        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
    }
}
