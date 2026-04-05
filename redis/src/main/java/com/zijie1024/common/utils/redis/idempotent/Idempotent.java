package com.zijie1024.common.utils.redis.idempotent;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 字节幺零二四
 * @date 2025-08-21 22:54
 * @description 接口幂等性校验注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 唯一标识的获取位置，默认从 Header 获取
     */
    TokenSource source() default TokenSource.HEADER;

    /**
     * 唯一标识的字段名
     */
    String tokenName() default "idempotent-token";

    /**
     * Redis Key 的前缀，用于命名空间隔离
     */
    String prefix() default "idempotent:req:";

    /**
     * 唯一标识的有效时间（用于异常回滚时重新塞入 Redis 的存活时间）
     * 注意：正常的存活时间由生成 Token 的接口决定，这里仅用于回滚补偿。
     */
    long expireTime() default 10;

    /**
     * 时间单位，默认分钟
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    /**
     * 指定哪些异常发生时，需要将 Token 重新塞回 Redis（允许客户端重试）
     * 默认空，即所有异常都不回滚（Fail-Safe）
     */
    Class<? extends Throwable>[] rollbackFor() default {};

    /**
     * 指定哪些异常发生时，绝对不回滚 Token（优先级高于 rollbackFor）
     */
    Class<? extends Throwable>[] noRollbackFor() default {};
}