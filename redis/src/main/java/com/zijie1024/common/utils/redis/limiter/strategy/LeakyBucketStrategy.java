package com.zijie1024.common.utils.redis.limiter.strategy;

import com.zijie1024.common.utils.redis.limiter.annotation.LeakyBucketLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 漏桶限流策略实现类
 *
 * @author 字节幺零二四
 * @date 2025-12-12 23:09
 * @description 基于 Redis Hash 结构和 Lua 脚本实现原子的漏桶限流算法
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LeakyBucketStrategy implements RateLimitStrategy<LeakyBucketLimit> {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 漏桶 Lua 脚本
     * 1. 接收参数：桶容量(capacity)、漏出速率(leak_rate, 滴/秒)、当前时间戳(now_ms)
     * 2. 从 Hash 中读取当前积压的水量(water)和上次计算时间(last_time)
     * 3. 计算距离上次访问流逝的时间，得出期间漏出了多少水，从而计算出当前桶内真实的剩余水量
     * 4. 尝试将当前请求（1滴水）放入桶中，若放入后超过容量上限则拒绝（溢出）
     * 5. 保存最新状态，并更新过期时间
     */
    private static final String LUA_SCRIPT =
            "local key = KEYS[1]\n" +
                    "local capacity = tonumber(ARGV[1])\n" +
                    "local leak_rate = tonumber(ARGV[2])\n" +
                    "local now_ms = tonumber(ARGV[3])\n" +
                    "\n" +
                    "local bucket = redis.call('HMGET', key, 'water', 'last_time')\n" +
                    "local water = tonumber(bucket[1]) or 0\n" +
                    "local last_time = tonumber(bucket[2]) or now_ms\n" +
                    "\n" +
                    "local delta_ms = math.max(0, now_ms - last_time)\n" +
                    "local leaked = delta_ms * leak_rate / 1000\n" +
                    "water = math.max(0, water - leaked)\n" +
                    "\n" +
                    "if water + 1 <= capacity then\n" +
                    "    water = water + 1\n" +
                    "    redis.call('HMSET', key, 'water', water, 'last_time', now_ms)\n" +
                    "    local ttl = math.ceil(capacity / leak_rate) + 1\n" +
                    "    redis.call('EXPIRE', key, ttl)\n" +
                    "    return 1\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";

    // 将 Lua 脚本字符串封装为可执行的 RedisScript 对象
    private static final DefaultRedisScript<Long> REDIS_SCRIPT = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

    /**
     * 声明当前策略支持的注解类型
     */
    @Override
    public Class<LeakyBucketLimit> supportAnnotation() {
        return LeakyBucketLimit.class;
    }

    /**
     * 执行漏桶限流逻辑
     *
     * @param combineKey 动态生成的 Redis 唯一 Key
     * @param annotation 目标方法上的漏桶注解实例
     * @return true 表示成功入桶，false 表示桶满溢出（触发限流）
     */
    @Override
    public boolean tryAcquire(String combineKey, LeakyBucketLimit annotation) {
        // 桶的最大容量
        int capacity = annotation.capacity();
        // 水漏出的恒定速率（每秒处理请求数）
        int leakRate = annotation.leakRate();
        // 获取 Java 端当前时间戳（毫秒），作为参考时间传入 Lua，规避 Redis TIME 命令在不同节点的一致性问题
        long nowMs = System.currentTimeMillis();
        try {
            // 执行 Lua 脚本完成原子判断
            Long result = stringRedisTemplate.execute(
                    REDIS_SCRIPT,
                    Collections.singletonList(combineKey),
                    String.valueOf(capacity),
                    String.valueOf(leakRate),
                    String.valueOf(nowMs)
            );
            // 返回 1 表示放入成功（放行），返回 0 表示溢出（限流拦截）
            return result != null && result == 1L;
        } catch (Exception e) {
            // 降级策略：Redis 执行异常时不阻塞核心业务流转
            log.error("漏桶限流策略执行异常，Key: {}, 予以放行", combineKey, e);
            return true;
        }
    }
}