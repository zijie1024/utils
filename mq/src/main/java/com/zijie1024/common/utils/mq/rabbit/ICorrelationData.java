package com.zijie1024.common.utils.mq.rabbit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.util.Map;

/**
 * @author 字节幺零二四
 * @date 2025-03-01 17:10
 * @description ICorrelationData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ICorrelationData extends CorrelationData {
    /**
     * 消息
     */
    private Object msg;
    /**
     * 交换机
     */
    private String exchange;
    /**
     * 路由
     */
    private String routingKey;
    /**
     * 可重试次数
     */
    private int retryCount;
    /**
     * 延迟时间
     */
    private Long delayTime;
    /**
     * 附加参数
     */
    private Map<String, String> args;
}
