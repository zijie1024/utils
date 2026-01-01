package com.zijie1024.common.utils.mq.redis.example;

import com.zijie1024.common.utils.mq.redis.bloom.BloomFilterManager;
import com.zijie1024.common.utils.mq.redis.cache.ICache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author 字节幺零二四
 * @date 2024-04-12 18:11
 * @description 分布式缓存使用示例
 */
@Service
@RequiredArgsConstructor
public class Example {
    private final BloomFilterManager bloomFilterManager;

    @ICache(prefix = "mine:data:", lockPrefix = "lock:data:")
    public String getData(Long id) {
        // 实际布隆过滤器放到缓存前更好，或者直接集成到ICache中
        boolean f = bloomFilterManager.contains("filterName", id);
        if (!f) return null;
        return "select data from db";
    }
}
