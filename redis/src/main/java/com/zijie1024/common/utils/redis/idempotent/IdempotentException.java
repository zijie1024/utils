package com.zijie1024.common.utils.redis.idempotent;

/**
 * @author 字节幺零二四
 * @date 2025-08-21 22:53
 * @description 幂等性校验异常
 */
public class IdempotentException extends RuntimeException {
    public IdempotentException(String message) {
        super(message);
    }
}