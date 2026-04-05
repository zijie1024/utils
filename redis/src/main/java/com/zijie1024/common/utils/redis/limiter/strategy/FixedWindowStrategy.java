package com.zijie1024.common.utils.redis.limiter.strategy;

import com.zijie1024.common.utils.redis.limiter.annotation.FixedWindowLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author 字节幺零二四
 * @date 2025-12-12 21:20
 * @description 固定窗口限流策略实现类。基于 Redis 和 Lua 脚本实现原子的固定窗口限流算法。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FixedWindowStrategy implements RateLimitStrategy<FixedWindowLimit> {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Lua 脚本：
     * 1. 获取当前 Key 的请求次数（如果没有则默认为 0）
     * 2. 判断当前请求次数是否已达到最大限制，如果达到则返回 0（代表被限流）
     * 3. 如果未达到限制，则将该 Key 的值自增 1
     * 4. 如果自增后的值是 1（代表这是当前时间窗口内的第一次请求），则为该 Key 设置过期时间
     * 5. 返回 1（代表放行）
     */
    private static final String LUA_SCRIPT =
            "local key = KEYS[1]\n" +
                    "local limit = tonumber(ARGV[1])\n" +
                    "local expire_time = tonumber(ARGV[2])\n" +
                    "local current = tonumber(redis.call('get', key) or '0')\n" +
                    "if current >= limit then\n" +
                    "    return 0\n" +
                    "end\n" +
                    "current = redis.call('incr', key)\n" +
                    "if current == 1 then\n" +
                    "    redis.call('expire', key, expire_time)\n" +
                    "end\n" +
                    "return 1";
    // 将 Lua 脚本字符串封装为可执行的 RedisScript 对象
    private static final DefaultRedisScript<Long> REDIS_SCRIPT = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

    /**
     * 声明当前策略支持的注解类型
     *
     * @return 固定窗口限流注解的 Class 对象
     */
    @Override
    public Class<FixedWindowLimit> supportAnnotation() {
        return FixedWindowLimit.class;
    }

    /**
     * 执行固定窗口限流的核心算法逻辑
     *
     * @param combineKey 动态生成的 Redis 限流唯一标识 Key
     * @param annotation 目标方法上实际标注的固定窗口限流注解实例，包含时间窗口大小、阈值等专属参数
     * @return true 表示允许访问（未超过阈值），false 表示触发限流规则（请求超限）
     */
    @Override
    public boolean tryAcquire(String combineKey, FixedWindowLimit annotation) {

        // 提取注解中配置的时间大小
        int time = annotation.time();
        // 提取注解中配置的时间单位
        TimeUnit unit = annotation.unit();

        // 将时间单位统一转换为秒，以便于 Redis 处理过期时间
        long expireSeconds = unit.toSeconds(time);

        // 校验转换后的秒数是否合法，避免配置了微秒/纳秒等极小单位导致转为 0，从而导致限流失效
        if (expireSeconds <= 0) {
            throw new IllegalArgumentException("Invalid time configuration. Minimum supported unit is seconds.");
        }

        // 获取时间窗口内允许的最大请求次数
        int limitCount = annotation.count();

        try {
            // 执行 Lua 脚本完成原子判断
            // 参数1：脚本对象；参数2：Key 列表（由 KEYS[1] 接收）；参数3及其后：ARGV 参数列表（分别对应限流次数、过期秒数）
            Long result = stringRedisTemplate.execute(
                    REDIS_SCRIPT,
                    Collections.singletonList(combineKey),
                    String.valueOf(limitCount),
                    String.valueOf(expireSeconds)
            );
            // 根据 Lua 脚本的返回值判断是否放行（返回 1 表示放行，0 表示限流）
            return result != null && result == 1L;
        } catch (Exception e) {
            // 发生异常时（如 Redis 宕机或网络抖动），为了保证核心业务的高可用，这里选择记录错误日志并降级放行
            log.error("固定窗口限流策略执行异常，Key: {}, 予以放行", combineKey, e);
            return true;
        }
    }
}