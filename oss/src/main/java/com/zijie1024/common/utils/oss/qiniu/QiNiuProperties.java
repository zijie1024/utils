package com.zijie1024.common.utils.oss.qiniu;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 字节幺零二四
 * @date 2025-02-15 20:48
 * @description QiNiuConfiguration
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mine.oss.qiniu")
public class QiNiuProperties {
    /**
     * accessKey
     */
    private String accessKey;
    /**
     * secretKey
     */
    private String secretKey;
    /**
     * bucket
     */
    private String bucket;
    /**
     * 绑定域名
     */
    private String domain;
    /**
     * 图片访问过期时间
     */
    private Long imageAccessExpirationTime;
}
