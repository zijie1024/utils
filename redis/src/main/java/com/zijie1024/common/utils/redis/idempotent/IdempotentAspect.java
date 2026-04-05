package com.zijie1024.common.utils.redis.idempotent;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author 字节幺零二四
 * @date 2025-08-21 22:56
 * @description 幂等性校验切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 环绕通知，执行幂等性校验核心逻辑
     *
     * @param joinPoint  切入点对象，包含被代理目标方法的信息和参数
     * @param idempotent 拦截到的自定义幂等性注解实例，包含相关配置信息
     * @return 目标方法的执行结果
     * @throws Throwable 当校验失败抛出 IdempotentException，或目标业务代码执行时抛出的异常
     */
    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {

        // 获取当前 HTTP 请求上下文
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IdempotentException("Failed to obtain HTTP request context");
        }
        HttpServletRequest req = attributes.getRequest();

        // 提取唯一标识
        String token = extractToken(req, idempotent);
        if (!StringUtils.hasText(token)) {
            throw new IdempotentException("Missing idempotency token: " + idempotent.tokenName());
        }

        // 拼接 Redis Key
        String redisKey = idempotent.prefix() + token;

        // 核心校验：尝试删除 Redis 中的 Token (原子操作)
        // delete 返回 true 说明 Key 存在且被成功删除（首次请求放行）
        // delete 返回 false 说明 Key 不存在或已被删除（重复请求拦截）
        Boolean success = stringRedisTemplate.delete(redisKey);
        if (Boolean.FALSE.equals(success)) {
            throw new IdempotentException("Duplicate request detected");
        }

        // 放行并执行业务逻辑
        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            // 判断是否需要回滚
            if (shouldRollback(ex, idempotent.rollbackFor(), idempotent.noRollbackFor())) {
                // 回滚策略：将 Token 重新塞回 Redis，值为占位符 "0"，并设置注解指定的过期时间
                stringRedisTemplate.opsForValue().set(
                        redisKey,
                        "0",
                        idempotent.expireTime(),
                        idempotent.timeUnit()
                );
            }
            // 将异常继续向上抛出，交由全局异常处理器处理
            throw ex;
        }
    }

    /**
     * 根据配置策略从请求中提取 Token
     *
     * @param req        当前的 HTTP 请求对象
     * @param idempotent 幂等性注解配置
     * @return 提取到的 Token 字符串，如果未找到则返回 null
     */
    private String extractToken(HttpServletRequest req, Idempotent idempotent) {
        if (idempotent.source() == TokenSource.HEADER) {
            return req.getHeader(idempotent.tokenName());
        } else if (idempotent.source() == TokenSource.PARAM) {
            return req.getParameter(idempotent.tokenName());
        }
        return null;
    }

    /**
     * 判定是否需要回滚 Token
     *
     * @param ex            实际抛出的异常实例
     * @param rollbackFor   配置的需要回滚的异常类型数组（白名单）
     * @param noRollbackFor 配置的绝对不回滚的异常类型数组（黑名单，优先级最高）
     * @return 满足回滚条件返回 true，否则返回 false
     */
    private boolean shouldRollback(Throwable ex,
                                   Class<? extends Throwable>[] rollbackFor,
                                   Class<? extends Throwable>[] noRollbackFor) {
        // 优先校验“绝对不回滚”名单（黑名单优先级最高）
        for (Class<? extends Throwable> noRollbackClass : noRollbackFor) {
            if (noRollbackClass.isAssignableFrom(ex.getClass())) {
                return false;
            }
        }

        // 校验“指定回滚”名单
        for (Class<? extends Throwable> rollbackClass : rollbackFor) {
            if (rollbackClass.isAssignableFrom(ex.getClass())) {
                return true;
            }
        }

        // 默认策略：非明确指定回滚的异常，一律不回滚
        return false;
    }
}