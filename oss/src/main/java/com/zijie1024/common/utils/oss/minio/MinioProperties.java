package com.zijie1024.common.utils.oss.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 字节幺零二四
 * @date 2025-02-16 18:21:39
 * @description MinioProperties
 */
@Data
@ConfigurationProperties(prefix = "mine.oss.minio")
public class MinioProperties {
    /**
     * Minio服务地址
     */
    private String endpoint;
    /**
     * accessKey
     */
    private String accessKey;
    /**
     * secretKey
     */
    private String secretKey;
    /**
     * 桶
     */
    private String bucket;
}
