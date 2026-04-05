package com.zijie1024.common.utils.redis.limiter.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 字节幺零二四
 * @date 2025-12-12 21:15
 * @description 限流策略注册中心
 * 在 Spring 容器初始化完成后，自动将所有实现了 RateLimitStrategy 的 Bean 加载到映射表中
 */
@Component
@RequiredArgsConstructor
public class RateLimitStrategyRegistry implements InitializingBean {

    private final Map<Class<? extends Annotation>, RateLimitStrategy<? extends Annotation>> strategyMap = new ConcurrentHashMap<>();
    private final List<RateLimitStrategy<?>> strategies;

    @Override
    public void afterPropertiesSet() {
        for (RateLimitStrategy<?> strategy : strategies) {
            // 优先级最高的最先进入循环
            strategyMap.putIfAbsent(strategy.supportAnnotation(), strategy);
        }
    }

    /**
     * 根据注解的 Class 类型获取对应的策略执行器
     *
     * @param annotationClass 注解的 Class 对象
     * @param <T>             注解泛型
     * @return 对应的策略实现类实例
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> RateLimitStrategy<T> getStrategy(Class<T> annotationClass) {
        return (RateLimitStrategy<T>) strategyMap.get(annotationClass);
    }
}