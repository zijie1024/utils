package com.zijie1024.common.utils.oss.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 字节幺零二四
 * @date 2025-02-16 18:21:39
 * @description MinioConfig
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {
    @Bean
    public MinioClient minioClient(MinioProperties properties) {
        MinioClient client = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        try {
            boolean isExist = client.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(properties.getBucket())
                            .build()
            );
            if (!isExist) {
                client.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(properties.getBucket())
                                .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create MinIO client", e);
        }
        return client;
    }
}
