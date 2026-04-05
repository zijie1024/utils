package com.zijie1024.common.utils.redis.limiter.strategy;

import com.zijie1024.common.utils.redis.limiter.annotation.SlidingWindowLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 滑动窗口限流策略实现类
 * @date 2025-12-12 23:28
 * @description 基于 Redis ZSet 结构和 Lua 脚本实现原子的滑动窗口限流算法。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlidingWindowStrategy implements RateLimitStrategy<SlidingWindowLimit> {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 滑动窗口 Lua 脚本
     * 1. 接收参数：当前时间戳(now_ms)、窗口起始时间(window_start_ms)、限流阈值(limit)、唯一请求标识(member_id)、窗口总时长(ttl_ms)
     * 2. 利用 ZREMRANGEBYSCORE 移除 ZSet 中 Score 小于 window_start_ms 的历史过期数据
     * 3. 利用 ZCARD 统计当前 ZSet 中剩余的元素数量（即当前有效窗口内的请求数）
     * 4. 判断请求数是否已达到阈值。如果达到，返回 0（限流）
     * 5. 如果未达到，利用 ZADD 将当前请求加入 ZSet，并利用 PEXPIRE 重置整个 Key 的过期时间为窗口时长
     * 6. 返回 1（放行）
     */
    private static final String LUA_SCRIPT =
            "local key = KEYS[1]\n" +
                    "local now_ms = tonumber(ARGV[1])\n" +
                    "local window_start_ms = tonumber(ARGV[2])\n" +
                    "local limit = tonumber(ARGV[3])\n" +
                    "local member_id = ARGV[4]\n" +
                    "local ttl_ms = tonumber(ARGV[5])\n" +
                    "\n" +
                    "redis.call('ZREMRANGEBYSCORE', key, '-inf', window_start_ms)\n" +
                    "\n" +
                    "local current_count = redis.call('ZCARD', key)\n" +
                    "\n" +
                    "if current_count >= limit then\n" +
                    "    return 0\n" +
                    "else\n" +
                    "    redis.call('ZADD', key, now_ms, member_id)\n" +
                    "    redis.call('PEXPIRE', key, ttl_ms)\n" +
                    "    return 1\n" +
                    "end";

    // 封装 Lua 脚本对象
    private static final DefaultRedisScript<Long> REDIS_SCRIPT = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

    /**
     * 声明当前策略支持的注解类型
     */
    @Override
    public Class<SlidingWindowLimit> supportAnnotation() {
        return SlidingWindowLimit.class;
    }

    /**
     * 执行滑动窗口限流逻辑
     *
     * @param combineKey 动态生成的 Redis 唯一 Key
     * @param annotation 目标方法上的滑动窗口注解实例
     * @return true 表示未超过窗口阈值，false 表示触发限流
     */
    @Override
    public boolean tryAcquire(String combineKey, SlidingWindowLimit annotation) {
        int time = annotation.time();
        TimeUnit unit = annotation.unit();
        int limitCount = annotation.count();

        // 统一转换为毫秒进行计算，保证滑动窗口的精度
        long windowDurationMs = unit.toMillis(time);
        if (windowDurationMs <= 0) {
            throw new IllegalArgumentException("Invalid time configuration. Minimum supported duration must be > 0.");
        }

        // 获取 Java 端当前毫秒时间戳
        long nowMs = System.currentTimeMillis();
        // 计算当前滑动窗口的有效起始时间戳
        long windowStartMs = nowMs - windowDurationMs;
        // 生成当前请求在 ZSet 中的唯一标识，防止并发情况下同一毫秒时间戳的请求互相覆盖
        String memberId = nowMs + "-" + UUID.randomUUID().toString();

        try {
            // 执行 Lua 脚本完成滑动窗口的原子判断
            Long result = stringRedisTemplate.execute(
                    REDIS_SCRIPT,
                    Collections.singletonList(combineKey),
                    String.valueOf(nowMs),
                    String.valueOf(windowStartMs),
                    String.valueOf(limitCount),
                    memberId,
                    String.valueOf(windowDurationMs)
            );

            // 1 表示放行，0 表示限流
            return result != null && result == 1L;
        } catch (Exception e) {
            // 降级策略：Redis 异常时不阻塞业务
            log.error("滑动窗口限流策略执行异常，Key: {}, 予以放行", combineKey, e);
            return true;
        }
    }
}