package com.zijie1024.common.utils.redis.limiter.annotation;

import com.zijie1024.common.utils.redis.limiter.enume.LimitScope;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 滑动窗口限流注解
 *
 * @author 字节幺零二四
 * @date 2025-12-12 23:27
 * @description 定义滑动窗口算法的专属参数，解决固定窗口的临界点突刺问题
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RateLimit
public @interface SlidingWindowLimit {

    /**
     * 继承自父注解：限流 Key 的前缀
     */
    @AliasFor(annotation = RateLimit.class, attribute = "key")
    String key() default "limit:sliding:";

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
     * 滑动窗口的时间跨度大小（配合 unit 属性使用）
     */
    int time() default 60;

    /**
     * 时间窗口内允许的最大请求次数
     */
    int count() default 100;
}
