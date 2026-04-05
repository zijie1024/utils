package com.zijie1024.common.utils.redis.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author 字节幺零二四
 * @date 2025-11-06 19:44
 * @description DistributedLockAspect
 */
@Aspect
@Component
public class DistributedLockAspect {

    private final DistributedLockFactory lockFactory;
    private final SpelExpressionParser spelParser;
    private final DefaultParameterNameDiscoverer nameDiscoverer;

    public DistributedLockAspect(DistributedLockFactory lockFactory) {
        this.lockFactory = lockFactory;
        this.spelParser = new SpelExpressionParser();
        this.nameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        // 解析 SpEL
        String dynamicKey = parseSpel(method, args, distributedLock.key());
        String finalLockKey = StringUtils.hasText(dynamicKey)
                ? distributedLock.prefix() + ":" + dynamicKey
                : distributedLock.prefix();

        // 通过工厂获取具体的锁实现
        RLock lock = lockFactory.getLock(distributedLock.type(), finalLockKey);

        boolean isLocked = false;
        try {
            // 尝试获取锁
            isLocked = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (isLocked) {
                // 获取锁成功，执行目标业务方法
                return joinPoint.proceed();
            } else {
                // 获取锁超时/失败
                throw new RuntimeException("Your request was interrupted. Please try again.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Oops, your request was unexpectedly interrupted. Please try again later.", e);
        } finally {
            // 安全释放锁
            if (isLocked && lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    /**
     * 解析 SpEL 表达式
     * SpEL 表达式举例："#userId + '-' + #productId"
     *
     * @param method         当前被 AOP 拦截到的目标方法
     * @param args           目标方法在本次执行时，实际传入的实参值数组
     * @param spelExpression 自定义注解中配置的 SpEL 表达式字符串
     * @return 解析计算后的最终字符串。如果未配置表达式或解析不到参数，则返回空字符串 ""
     */
    private String parseSpel(Method method, Object[] args, String spelExpression) {

        if (!StringUtils.hasText(spelExpression)) return "";

        // 通过字节码解析获取目标方法的形参名称数组（例如：["userId", "productId"]）
        String[] params = nameDiscoverer.getParameterNames(method);

        // 无参方法直接返回空
        if (params.length == 0) return "";

        // 实例化一个标准的 SpEL 上下文环境
        EvaluationContext context = new StandardEvaluationContext();

        // 建立“变量名 -> 变量值”的映射关系
        for (int i = 0; i < params.length; i++) context.setVariable(params[i], args[i]);

        // 解析表达式
        Expression expression = spelParser.parseExpression(spelExpression);

        // 计算表达式的具体值
        Object value = expression.getValue(context);

        // 结果转换
        return value != null ? value.toString() : "";
    }
}