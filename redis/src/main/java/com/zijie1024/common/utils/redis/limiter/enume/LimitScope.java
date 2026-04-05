package com.zijie1024.common.utils.redis.limiter.enume;

/**
 * @author 字节幺零二四
 * @date 2025-12-12 20:57
 * @description 限流范围策略枚举
 */
public enum LimitScope {
    /**
     * 全局接口级别限流
     */
    GLOBAL,

    /**
     * 基于单 IP 级别限流
     */
    IP
}