package com.zijie1024.common.utils.mq.rabbit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * @author 字节幺零二四
 * @date 2025-03-01 17:22
 * @description RabbitMqProperties
 */
@Data
@Component
@ConfigurationProperties(prefix = "mine.mq.rabbit")
public class IRabbitProperties {
    /**
     * 默认重试次数
     */
    private int retryCount = 5;

    /**
     * 延迟消息配置
     */
    @NestedConfigurationProperty
    private DelayProperties delay = new DelayProperties();

    /**
     * Redis配置
     */
    @NestedConfigurationProperty
    private RedisProperties redis = new RedisProperties();

    /**
     * 附加参数配置
     */
    @NestedConfigurationProperty
    private HeaderProperties header = new HeaderProperties();

    @Data
    static class DelayProperties {
        /**
         * 是否默认为延迟消息
         */
        private boolean enable = false;
        /**
         * 默认延迟时间，单位：秒
         */
        private int time = 60 * 60;
    }

    @Data
    static class RedisProperties {
        /**
         * 消息缓存到Redis的过期时间，单位：秒
         */
        private int expireTime = 60 * 60;
        /**
         * 缓存到Redis的消息的Key的前缀
         */
        private String prefix = "rabbit:";
    }


    @Data
    static class HeaderProperties {
        /**
         * boolean isDelay = messageProperties.getHeader(delayKey)，自定义
         */
        private String delayFlagKey = "is-delay";
        /**
         * String id = messageProperties.getHeader(idKey)，定值
         */
        private String correlationIdKey = "spring_returned_message_correlation";
        /**
         * String id = messageProperties.getHeader(idKey)，定值
         */
        private String businessIdKey = "business-id";
    }

}
