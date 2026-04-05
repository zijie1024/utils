package com.zijie1024.common.utils.redis.limiter.annotation;

import com.zijie1024.common.utils.redis.limiter.enume.LimitScope;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 字节幺零二四
 * @date 2025-12-12 21:00
 * @description 限流父注解，定义所有限流算法通用的属性
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流 Key 的前缀
     */
    String key() default "rate_limit:";

    /**
     * 限流范围策略
     */
    LimitScope scope() default LimitScope.GLOBAL;

    /**
     * 限流时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 触发限流时的提示信息
     */
    String message() default "Too many requests, please try again later.";
}