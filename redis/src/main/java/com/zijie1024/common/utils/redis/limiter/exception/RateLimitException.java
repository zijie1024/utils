package com.zijie1024.common.utils.redis.limiter.exception;

/**
 * @author 字节幺零二四
 * @date 2025-12-12 20:58
 * @description RateLimitException
 */
public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}