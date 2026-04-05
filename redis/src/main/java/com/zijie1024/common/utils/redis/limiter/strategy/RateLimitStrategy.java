package com.zijie1024.common.utils.redis.limiter.strategy;

import java.lang.annotation.Annotation;

/**
 *
 * @param <T> 具体的子注解类型
 * @author 字节幺零二四
 * @date 2025-12-12 21:13
 * @description 限流策略的统一标准接口
 */
public interface RateLimitStrategy<T extends Annotation> {

    /**
     * 声明当前策略支持处理的注解类型
     *
     * @return 支持的注解 Class 对象
     */
    Class<T> supportAnnotation();

    /**
     * 执行具体的限流算法逻辑
     *
     * @param combineKey 已经拼接完成的 Redis Key
     * @param annotation 包含专属参数的子注解实例
     * @return true 表示放行，false 表示被限流拦截
     */
    boolean tryAcquire(String combineKey, T annotation);
}