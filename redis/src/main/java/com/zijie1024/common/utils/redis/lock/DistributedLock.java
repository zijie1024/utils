package com.zijie1024.common.utils.redis.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解
 *
 * @author 字节幺零二四
 * @date 2025-11-06 19:42
 * @description DistributedLock
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 锁的 Key 前缀（例如："order:create"）
     */
    String prefix();

    /**
     * 动态 Key 的 SpEL 表达式（例如："#orderId" 或 "#user.id"）
     * 如果为空，则只使用 prefix 作为分布式锁的 Key
     */
    String key() default "";

    /**
     * 锁的类型，默认为可重入锁
     */
    LockType type() default LockType.REENTRANT;

    /**
     * 获取锁的最大等待时间。如果在该时间内未获取到锁，则直接放弃并抛出异常。
     */
    long waitTime() default 3;

    /**
     * 锁的持有时间。
     * 设为 -1 表示开启 Redisson 的看门狗机制（Watchdog），会自动续期。
     * 如果设置为大于 0 的值，则不开启看门狗，到期自动释放。
     */
    long leaseTime() default -1;

    /**
     * 时间单位，默认为秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}