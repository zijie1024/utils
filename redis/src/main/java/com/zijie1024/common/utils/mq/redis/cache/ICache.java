package com.zijie1024.common.utils.mq.redis.cache;

import java.lang.annotation.*;

/**
 * @author 字节幺零二四
 * @date 2024-04-12 17:25:49
 * @description 分布式缓存
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ICache {

    /**
     * 缓存前缀
     */
    String prefix() default "cache:";

    /**
     * 缓存后缀
     */
    String suffix() default "";

    /**
     * 过期时间，单位，秒
     */
    long expireTime() default 60;

    /**
     * 锁前缀
     */
    String lockPrefix() default "lock:";

    /**
     * 锁后缀
     */
    String lockSuffix() default "";

    /**
     * BloomFilter名称
     * TODO 查询缓存前可使用布隆过滤器判断数据是否存在
     */
    String bloomFilter() default "";
}
