package com.zijie1024.common.utils.redis.limiter.strategy;

import com.zijie1024.common.utils.redis.limiter.annotation.TokenBucketLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 令牌桶限流策略实现类 (基于纯 Redis Lua 脚本)
 *
 * @author 字节幺零二四
 * @date 2025-12-12 22:08
 * @description 利用 Redis 的 Hash 结构和 Lua 脚本，手动维护桶内令牌数和上次刷新时间，实现原子的令牌桶算法
 */
@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class TokenBucketLuaStrategy implements RateLimitStrategy<TokenBucketLimit> {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 令牌桶 Lua 脚本
     * 1. 接收参数：生成速率(rate)、桶最大容量(capacity)、当前时间戳毫秒值(now)
     * 2. 从 Hash 结构中读取当前桶内剩余令牌数(tokens)和上次更新时间(last_refreshed)
     * 3. 如果是第一次访问，初始化满桶
     * 4. 根据当前时间与上次更新时间的差值，计算并放入新生成的令牌
     * 5. 判断当前令牌数是否足够扣减，并更新状态到 Redis
     */
    private static final String LUA_SCRIPT =
            "local key = KEYS[1]\n" +
                    "local rate = tonumber(ARGV[1])\n" +
                    "local capacity = tonumber(ARGV[2])\n" +
                    "local now_ms = tonumber(ARGV[3])\n" +
                    "local requested = 1\n" +
                    "\n" +
                    "local bucket = redis.call('HMGET', key, 'tokens', 'last_refreshed')\n" +
                    "local tokens = tonumber(bucket[1])\n" +
                    "local last_refreshed = tonumber(bucket[2])\n" +
                    "\n" +
                    "if tokens == nil then\n" +
                    "    tokens = capacity\n" +
                    "    last_refreshed = now_ms\n" +
                    "end\n" +
                    "\n" +
                    "local delta_ms = math.max(0, now_ms - last_refreshed)\n" +
                    "local tokens_to_add = math.floor(delta_ms * rate / 1000)\n" +
                    "\n" +
                    "if tokens_to_add > 0 then\n" +
                    "    tokens = math.min(capacity, tokens + tokens_to_add)\n" +
                    "    last_refreshed = now_ms\n" +
                    "end\n" +
                    "\n" +
                    "if tokens >= requested then\n" +
                    "    tokens = tokens - requested\n" +
                    "    redis.call('HMSET', key, 'tokens', tokens, 'last_refreshed', last_refreshed)\n" +
                    "    local ttl = math.ceil(capacity / rate) + 1\n" +
                    "    redis.call('EXPIRE', key, ttl)\n" +
                    "    return 1\n" +
                    "else\n" +
                    "    redis.call('HMSET', key, 'tokens', tokens, 'last_refreshed', last_refreshed)\n" +
                    "    local ttl = math.ceil(capacity / rate) + 1\n" +
                    "    redis.call('EXPIRE', key, ttl)\n" +
                    "    return 0\n" +
                    "end";

    private static final DefaultRedisScript<Long> REDIS_SCRIPT = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

    @Override
    public Class<TokenBucketLimit> supportAnnotation() {
        return TokenBucketLimit.class;
    }

    /**
     * 执行令牌桶限流逻辑
     *
     * @param combineKey 动态生成的 Redis 唯一 Key
     * @param annotation 目标方法上的令牌桶注解实例
     * @return true 表示成功获取到令牌，false 表示桶空被限流
     */
    @Override
    public boolean tryAcquire(String combineKey, TokenBucketLimit annotation) {
        // 每秒生成的令牌速率
        int replenishRate = annotation.replenishRate();
        // 桶的最大容量
        int burstCapacity = annotation.burstCapacity();
        // 获取当前时间戳（毫秒）
        long nowMs = System.currentTimeMillis();
        try {
            // 执行 Lua 脚本
            Long result = stringRedisTemplate.execute(
                    REDIS_SCRIPT,
                    Collections.singletonList(combineKey),
                    String.valueOf(replenishRate),
                    String.valueOf(burstCapacity),
                    String.valueOf(nowMs)
            );
            // 返回 1 表示扣减成功，返回 0 表示令牌不足
            return result != null && result == 1L;
        } catch (Exception e) {
            // 降级策略：Redis 异常时放行
            log.error("令牌桶限流策略执行异常，Key: {}, 予以放行", combineKey, e);
            return true;
        }
    }
}