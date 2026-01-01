package com.zijie1024.common.utils.mq.redis.cache;

import java.lang.annotation.*;

/**
 * @author 字节幺零二四
 * @date 2024-04-12 17:25:49
 * @description 移除分布式缓存
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ICacheRemove {
    /**
     * 缓存前缀
     */
    String prefix() default "cache:";

    /**
     * 缓存后缀
     */
    String suffix() default "";
}
