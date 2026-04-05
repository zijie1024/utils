package com.zijie1024.common.utils.redis.lock;

/**
 * @author 字节幺零二四
 * @date 2025-11-06 19:39
 * @description LockType
 */
public enum LockType {
    /**
     * 可重入锁
     */
    REENTRANT,
    /**
     * 公平锁
     */
    FAIR,
    /**
     * 读锁
     */
    READ,
    /**
     * 写锁
     */
    WRITE
}
