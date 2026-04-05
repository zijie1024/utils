package com.zijie1024.common.utils.redis.idempotent;

/**
 * @author 字节幺零二四
 * @date 2025-08-21 22:53
 * @description 唯一标识获取策略
 */
public enum TokenSource {
    /**
     * 从 HTTP 请求头中获取
     */
    HEADER,

    /**
     * 从 HTTP 请求参数中获取 (Parameter)
     */
    PARAM
}