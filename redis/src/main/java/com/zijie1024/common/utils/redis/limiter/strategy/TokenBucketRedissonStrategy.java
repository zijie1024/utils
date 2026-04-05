package com.zijie1024.common.utils.redis.limiter.strategy;


import com.zijie1024.common.utils.redis.limiter.annotation.TokenBucketLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 令牌桶限流策略实现类 (基于 Redisson 客户端)
 *
 * @author 字节幺零二四
 * @date 2025-12-12 22:12
 * @description 使用 Redisson 框架内置的 RRateLimiter 组件实现高可靠的限流控制。
 */
@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class TokenBucketRedissonStrategy implements RateLimitStrategy<TokenBucketLimit> {

    // 注入 Redisson 客户端对象
    private final RedissonClient redissonClient;

    @Override
    public Class<TokenBucketLimit> supportAnnotation() {
        return TokenBucketLimit.class;
    }

    /**
     * 执行令牌桶限流逻辑
     *
     * @param combineKey 动态生成的 Redis 唯一 Key
     * @param annotation 目标方法上的令牌桶注解实例
     * @return true 表示未触发限流，false 表示触发限流
     */
    @Override
    public boolean tryAcquire(String combineKey, TokenBucketLimit annotation) {
        // 对于 Redisson，我们使用突发容量（burstCapacity）作为一段时间内发放的总令牌数
        int capacity = annotation.burstCapacity();
        try {
            // 获取 Redisson 的限流器实例
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(combineKey);
            // 尝试初始化限流器的参数（只有第一次或者参数改变时才会初始化生效）
            // RateType.OVERALL 表示全局限流（所有服务实例共享）
            // 设置每秒（RateIntervalUnit.SECONDS）允许通过的令牌数为 capacity
            rateLimiter.trySetRate(RateType.OVERALL, capacity, 1, RateIntervalUnit.SECONDS);
            // 尝试获取 1 个令牌，如果获取成功返回 true，失败返回 false (非阻塞操作)
            return rateLimiter.tryAcquire(1);
        } catch (Exception e) {
            // 降级策略：限流器异常时不阻塞主业务
            log.error("Redisson 限流策略执行异常，Key: {}, 予以放行", combineKey, e);
            return true;
        }
    }
}