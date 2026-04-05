package com.zijie1024.common.utils.redis.limiter.annotation;

import com.zijie1024.common.utils.redis.limiter.enume.LimitScope;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 漏桶限流
 *
 * @author 字节幺零二四
 * @date 2025-12-12 23:07
 * @description 定义漏桶算法的专属参数：桶容量与漏出速率
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RateLimit
public @interface LeakyBucketLimit {

    /**
     * 继承自父注解：限流 Key 的前缀
     */
    @AliasFor(annotation = RateLimit.class, attribute = "key")
    String key() default "limit:leaky:";

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
    String message() default "The queue is full, please try again later.";

    /**
     * 漏桶的最大容量（代表最多允许多少个请求同时在积压排队）
     */
    int capacity() default 20;

    /**
     * 水漏出的速率（代表系统每秒处理请求的恒定速率）
     */
    int leakRate() default 10;
}