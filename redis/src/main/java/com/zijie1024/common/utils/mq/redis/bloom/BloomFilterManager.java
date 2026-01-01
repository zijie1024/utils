package com.zijie1024.common.utils.mq.redis.bloom;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author 字节幺零二四
 * @date 2024-04-12 17:29:12
 * @description CacheManager
 */
@Component
@RequiredArgsConstructor
public class BloomFilterManager {

    private final ICacheUtil$RedissonConfig config;

    private final RedissonClient redissonClient;

    @PostConstruct
    private void init() {
        // 初始化布隆过滤器
        config.filters.forEach(it -> {
            RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(it.name);
            bloomFilter.delete();
            bloomFilter.tryInit(it.insertion, it.probability);
        });
    }

    /**
     * 获取BloomFilter
     *
     * @param filterName 名称
     * @return BloomFilter
     */
    public <T> RBloomFilter<T> getBloomFilter(String filterName) {
        return redissonClient.getBloomFilter(filterName);
    }

    /**
     * 标记数据
     *
     * @param filterName 名称
     * @param data       数据
     */
    public <T> void add(String filterName, T data) {
        redissonClient.getBloomFilter(filterName).add(data);
    }


    /**
     * 标记数据
     *
     * @param filterName 名称
     * @param collection 数据集合
     */
    public <T> void addBatch(String filterName, Collection<T> collection) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(filterName);
        collection.forEach(filter::add);
    }

    /**
     * 标记数据
     *
     * @param filterName 名称
     * @param collection 数据集合
     */
    public <T> void reset(String filterName, Collection<T> collection) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(filterName);
        long expectedInsertions = filter.getExpectedInsertions();
        double falseProbability = filter.getFalseProbability();
        filter.delete();
        filter.tryInit(expectedInsertions, falseProbability);
        collection.forEach(filter::add);
    }


    /**
     * 判断数据是否存在
     *
     * @param filterName 名称
     * @param data       数据
     * @return true if exists else false
     */
    public boolean contains(String filterName, Object data) {
        return redissonClient.getBloomFilter(filterName).contains(data);
    }


    @Data
    @Component
    @ConfigurationProperties(prefix = "mine.cache.redis.bloom")
    static class ICacheUtil$RedissonConfig {

        private Boolean enable;
        private List<BloomFilter> filters;

        @Data
        static class BloomFilter {
            private String name;
            private long insertion;
            private double probability;
        }
    }

}
