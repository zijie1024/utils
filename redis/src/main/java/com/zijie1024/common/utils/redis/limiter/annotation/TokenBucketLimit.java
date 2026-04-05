package com.zijie1024.common.utils.redis.limiter.annotation;

import com.zijie1024.common.utils.redis.limiter.enume.LimitScope;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 字节幺零二四
 * @date 2025-12-12 21:12
 * @description 令牌桶限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RateLimit
public @interface TokenBucketLimit {

    /**
     * 继承自父注解：限流 Key 的前缀
     */
    @AliasFor(annotation = RateLimit.class, attribute = "key")
    String key() default "limit:token:";

    /**
     * 继承自父注解：限流范围策略
     */
    @AliasFor(annotation = RateLimit.class, attribute = "scope")
    LimitScope scope() default LimitScope.GLOBAL;

    /**
     * 继承自父注解：限流时间单位
     */
    @AliasFor(annotation = RateLimit.class, attribute = "unit")
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 继承自父注解：触发限流时的提示信息
     */
    @AliasFor(annotation = RateLimit.class, attribute = "message")
    String message() default "System is busy, please try again later.";

    /**
     * 令牌每秒生成的速率
     */
    int replenishRate() default 10;

    /**
     * 令牌桶的最大容量（允许应对的突发流量）
     */
    int burstCapacity() default 20;
}