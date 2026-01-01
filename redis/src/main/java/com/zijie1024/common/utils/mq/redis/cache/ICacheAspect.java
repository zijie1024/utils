package com.zijie1024.common.utils.mq.redis.cache;


import com.zijie1024.common.utils.mq.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * @author 字节幺零二四
 * @date 2024-04-12 17:29:12
 * @description 分布式缓存aop
 */
@Component
@Aspect
@EnableConfigurationProperties(ICacheProperties.class)
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class ICacheAspect {

    private final RedissonClient redissonClient;

    private final StringRedisTemplate redisTemplate;

    private final ICacheProperties properties;


    /**
     * 环绕通知
     */
    @SneakyThrows
    @Around("@annotation(iCache)")
    public Object ICacheAspectMethod(ProceedingJoinPoint point, ICache iCache) {
        // 返回对象
        Object obj;
        // 添加注解的方法
        MethodSignature ms = (MethodSignature) point.getSignature();
        // 注解上指定的缓存的key的前缀和后缀
        String prefix = iCache.prefix();
        String suffix = iCache.suffix();
        // 注解上指定的缓存的过期时间
        long expireTime = iCache.expireTime();
        // 注解上指定的锁的key的前缀和后缀
        String lockPrefix = iCache.lockPrefix();
        String lockSuffix = iCache.lockSuffix();
        // 方法的参数
        Object[] args = point.getArgs();
        // 拼接缓存的key
        String key = prefix + Arrays.asList(args) + suffix;
        // 是否开启缓存
        if (Boolean.FALSE.equals(properties.getEnable())) return point.proceed(point.getArgs());
        try {
            while (true) {
                // 从redis中获取缓存数据
                obj = getCache(key, ms);
                // 有缓存则直接返回数据，无缓存则需要查询数据库
                if (obj != null) return obj;
                // 分布式锁
                RLock lock = redissonClient.getLock(lockPrefix + key + lockSuffix);
                boolean flag = lock.tryLock(properties.getLockWait(), TimeUnit.SECONDS);
                //  判断是否获取到锁
                if (flag) {
                    try {
                        // 执行业务逻辑（直接从数据库获取数据）
                        obj = point.proceed(point.getArgs());
                        // 无数据
                        if (obj == null) {
                            // 缓存空对象（转JSON存储）：缓解缓存穿透
                            // 不能使用new Object()，否者会出现类型转换异常
                            obj = ms.getReturnType().getDeclaredConstructor().newInstance();
                            expireTime = properties.getNullTimeout();
                        }
                        // 转JSON存储
                        this.redisTemplate.opsForValue().set(
                                key,
                                JsonUtil.stringify(obj),
                                expireTime,
                                TimeUnit.SECONDS
                        );
                        // 返回结果
                        return obj;
                    } finally {
                        //  释放锁
                        lock.unlock();
                    }
                } else {
                    // 等待，自旋
                    Thread.sleep(properties.getLockSleep());
                }
            }
        } catch (Throwable t) {
            // TODO 写日志：缓存失效
            throw new RuntimeException(t);
            //  数据库兜底
            // return point.proceed(point.getArgs());
        }
    }


    /**
     * 后置通知
     */
    @SneakyThrows
    @After("@annotation(iCacheRemove)")
    public void ICacheAspectMethod(JoinPoint point, ICacheRemove iCacheRemove) {
        // 是否开启缓存
        if (Boolean.FALSE.equals(properties.getEnable())) return;
        // 添加注解的方法
        MethodSignature ms = (MethodSignature) point.getSignature();
        // 注解上指定的缓存的key的前缀和后缀
        String prefix = iCacheRemove.prefix();
        String suffix = iCacheRemove.suffix();
        // 方法的参数
        Object[] args = point.getArgs();
        // 拼接缓存的key
        String key = prefix + Arrays.asList(args) + suffix;
        // 清除缓存
        redisTemplate.delete(key);
    }

    /**
     * 从Redis缓存中获取缓存
     *
     * @param key 缓存的key
     * @param ms  切入的方法
     * @return 缓存的数据
     */
    private Object getCache(String key, MethodSignature ms) {
        // 存数据时存的是JSON，取数据时取出的也是JSON
        String strJson = (String) redisTemplate.opsForValue().get(key);
        //  判断
        if (strJson != null && !strJson.isBlank()) {
            // 将字符串转换为对应的数据类型
            return JsonUtil.parse(strJson, ms.getReturnType());
        }
        return null;
    }
}
