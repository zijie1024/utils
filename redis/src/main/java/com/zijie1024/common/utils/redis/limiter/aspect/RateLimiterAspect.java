package com.zijie1024.common.utils.redis.limiter.aspect;

import com.zijie1024.common.utils.com.net.NetUtil;
import com.zijie1024.common.utils.redis.limiter.annotation.RateLimit;
import com.zijie1024.common.utils.redis.limiter.enume.LimitScope;
import com.zijie1024.common.utils.redis.limiter.exception.RateLimitException;
import com.zijie1024.common.utils.redis.limiter.strategy.RateLimitStrategy;
import com.zijie1024.common.utils.redis.limiter.strategy.RateLimitStrategyRegistry;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author 字节幺零二四
 * @date 2025-12-12 21:17
 * @description 负责拦截带有特定限流注解的请求，并路由给匹配的策略实现类
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect {

    private final RateLimitStrategyRegistry registry;

    /**
     * 定义切入点
     */
    @Pointcut("@annotation(com.zijie1024.common.utils.redis.limiter.annotation.FixedWindowLimit) || " +
            "@annotation(com.zijie1024.common.utils.redis.limiter.annotation.SlidingWindowLimit) || " +
            "@annotation(com.zijie1024.common.utils.redis.limiter.annotation.LeakyBucketLimit) || " +
            "@annotation(com.zijie1024.common.utils.redis.limiter.annotation.TokenBucketLimit)")
    public void rateLimitPointcut() {
    }

    /**
     * 解析注解参数、生成 Redis Key，并调用对应的限流算法逻辑。
     *
     * @param joinPoint AOP 切点对象，包含目标对象、方法参数等上下文信息
     * @return 目标方法的真实执行结果
     * @throws Throwable 目标方法自身抛出的异常，或请求被限流时抛出的 RateLimitException
     */
    @Around("rateLimitPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // 利用 Spring 工具类提取合并后的元注解配置
        RateLimit parentConfig = AnnotatedElementUtils.findMergedAnnotation(method, RateLimit.class);
        if (parentConfig == null) return joinPoint.proceed();

        // 声明目标子注解及其对应的策略执行器实例
        Annotation targetChildAnnotation = null;
        RateLimitStrategy<?> strategy = null;

        // 遍历目标方法上的所有注解，在注册表中寻找支持该注解的限流策略
        for (Annotation annotation : method.getAnnotations()) {
            strategy = registry.getStrategy(annotation.annotationType());
            if (strategy != null) {
                targetChildAnnotation = annotation;
                break;
            }
        }

        // 如果成功匹配到了子注解和策略执行器，则开始执行后续限流判断逻辑
        if (targetChildAnnotation != null) {
            // 根据提取出的通用配置和当前上下文，构建用于 Redis 缓存操作的动态 Key
            String combineKey = buildCombineKey(
                    parentConfig.key(),
                    parentConfig.scope(),
                    targetChildAnnotation.annotationType().getSimpleName(),
                    joinPoint,
                    method
            );
            boolean isAllowed = executeStrategy(strategy, combineKey, targetChildAnnotation);
            if (!isAllowed) {
                throw new RateLimitException(parentConfig.message());
            }
        }

        return joinPoint.proceed();
    }

    /**
     * 专门处理泛型转换的私有方法，避免主逻辑出现严重的泛型擦除警告或类型报错
     *
     * @param strategy   从注册表中匹配到的限流策略（无具体泛型）
     * @param combineKey 动态生成的 Redis 限流唯一标识 Key
     * @param annotation 目标方法上实际标注的具体限流子注解实例
     * @param <T>        注解的类型泛型
     * @return true 表示允许访问，false 表示触发限流规则
     */
    @SuppressWarnings("unchecked")
    private <T extends Annotation> boolean executeStrategy(RateLimitStrategy<?> strategy, String combineKey, Annotation annotation) {
        RateLimitStrategy<T> typedStrategy = (RateLimitStrategy<T>) strategy;
        return typedStrategy.tryAcquire(combineKey, (T) annotation);
    }

    /**
     * 根据基础配置和请求上下文构建动态组合的 Redis Key
     * 格式约定为：[前缀]:[子注解类名]:[IP地址]:[目标类名]-[目标方法名]
     *
     * @param prefix   限流 Key 的业务通用前缀
     * @param scope    限流的作用域范围
     * @param typeName 具体限流子注解的简单类名
     * @param point    AOP 切面上下文，用于提取被代理类的类名信息
     * @param method   当前拦截的目标方法，用于提取方法名
     * @return 最终拼接完成的 Redis Key 字符串
     */
    private String buildCombineKey(String prefix, LimitScope scope, String typeName, ProceedingJoinPoint point, Method method) {
        StringBuilder sb = new StringBuilder(prefix).append(typeName).append(":");
        if (scope == LimitScope.IP) {
            String ip = NetUtil.getIpAddress();
            if (NetUtil.UNKNOWN_IP.equals(ip)) {
                // Plan A：直接抛出异常，拒绝非法调用
                // throw new IllegalArgumentException("Unable to retrieve client IP for IP-based rate limiting.");
                // Plan B：降级为全局限流
                sb.append("global_fallback").append(":");
            }
        }
        sb.append(point.getTarget().getClass().getSimpleName())
                .append("-")
                .append(method.getName());
        return sb.toString();
    }
}