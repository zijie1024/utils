package com.zijie1024.common.utils.mq.rabbit;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author 字节幺零二四
 * @date 2025-03-01 20:12
 * @description IRabbitConfig
 */
@Configuration
@EnableConfigurationProperties(IRabbitProperties.class)
@RequiredArgsConstructor
public class IRabbitConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    private final RabbitTemplate rabbitTemplate;
    private final IRabbitProperties properties;

    @PostConstruct
    public void before() {
        this.rabbitTemplate.setConfirmCallback(this);
        this.rabbitTemplate.setReturnsCallback(this);
    }


    /**
     * 交换机确认回调方法：确认消息是否成功到达交换机
     *
     * @param data  消息id及相关信息
     * @param ack   对生产者的应答（true表示交换机成功收到消息）
     * @param cause 未到达的原因
     */
    @Override
    public void confirm(CorrelationData data, boolean ack, String cause) {
        if (!ack) this.retry(data);
    }

    @Override
    public void returnedMessage(ReturnedMessage rm) {
        MessageProperties mp = rm.getMessage().getMessageProperties();
        // 拦截延迟消息：延迟消息会强制触发returnedMessage
        if (Boolean.parseBoolean(mp.getHeader(properties.getHeader().getDelayFlagKey()))) return;
        ICorrelationData icd = IRabbitHelper.getMsgCache(mp.getCorrelationId());
        this.retry(icd);
    }

    /**
     * 重新发送消息
     */
    private void retry(CorrelationData cd) {
        ICorrelationData icd = (ICorrelationData) cd;
        int retryCount = icd.getRetryCount();
        if (retryCount > 0) {
            icd.setRetryCount(--retryCount);
            IRabbitHelper.setMsgCache(icd.getId(), icd);
            // 延迟消息
            boolean isDelay = icd.getDelayTime() > 0;
            rabbitTemplate.convertAndSend(
                    icd.getExchange(),
                    icd.getRoutingKey(),
                    icd.getMsg(),
                    m -> {
                        MessageProperties mp = m.getMessageProperties();
                        if (isDelay) mp.setDelayLong(TimeUnit.SECONDS.toMillis(icd.getDelayTime()));
                        icd.getArgs().forEach(mp::setHeader);
                        return m;
                    },
                    icd
            );
        } else IRabbitHelper.delMsgCache(icd.getId());
    }
}
