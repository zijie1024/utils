package com.zijie1024.common.utils.redis.lock;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * @author 字节幺零二四
 * @date 2025-11-06 19:42
 * @description DistributedLockFactory
 */
@Component
@RequiredArgsConstructor
public class DistributedLockFactory {

    private final RedissonClient redissonClient;

    /**
     * 根据类型获取对应的 RLock 对象
     *
     * @param lockType 锁的类型
     * @param lockKey  Key
     * @return 分布式锁
     */
    public RLock getLock(LockType lockType, String lockKey) {
        return switch (lockType) {
            case FAIR -> redissonClient.getFairLock(lockKey);
            case READ -> redissonClient.getReadWriteLock(lockKey).readLock();
            case WRITE -> redissonClient.getReadWriteLock(lockKey).writeLock();
            default -> redissonClient.getLock(lockKey);
        };
    }
}
