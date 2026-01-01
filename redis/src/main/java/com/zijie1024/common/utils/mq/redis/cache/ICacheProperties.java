package com.zijie1024.common.utils.mq.redis.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 字节幺零二四
 * @date 2026-01-01 14:47
 * @description ICacheProperties
 */
@Data
@ConfigurationProperties(prefix = "mine.cache")
public class ICacheProperties {
    /**
     * 是否开启缓存
     */
    private Boolean enable = false;

    /**
     * 空值缓存过期时间，单位：秒
     */
    private long nullTimeout = 60;

    /**
     * 尝试获取锁的等待时间，单位：秒
     */
    private long lockWait = 2;

    /**
     * 获取不到锁时自旋的时间，单位：秒
     */
    private long lockSleep = 1;
}
