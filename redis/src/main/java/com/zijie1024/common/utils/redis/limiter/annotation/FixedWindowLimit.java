package com.zijie1024.common.utils.redis.limiter.annotation;

import com.zijie1024.common.utils.redis.limiter.enume.LimitScope;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 字节幺零二四
 * @date 2025-12-12 21:03
 * @description 固定窗口限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RateLimit
public @interface FixedWindowLimit {

    /**
     * 继承自父注解：限流 Key 的前缀
     */
    @AliasFor(annotation = RateLimit.class, attribute = "key")
    String key() default "limit:fixed:";

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
    String message() default "Too many requests, please try again later.";

    /**
     * 时间窗口大小
     */
    int time() default 60;

    /**
     * 窗口内允许的最大请求次数
     */
    int count() default 100;

}