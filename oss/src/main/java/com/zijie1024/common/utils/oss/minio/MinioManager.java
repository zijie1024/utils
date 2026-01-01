package com.zijie1024.common.utils.oss.minio;

 import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @author 字节幺零二四
 * @date 2025-02-16 18:21:39
 * @description MinioManager
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioManager {

    private final MinioClient client;
    private final MinioProperties properties;

    /**
     * 上传文件
     *
     * @param file 文件
     * @param name 文件名，若不指定，则采用文件hash值作为文件名
     * @return 文件hash
     */
    public String upload(MultipartFile file, String name) {
        try {
            ObjectWriteResponse resp = client.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(name)
                    .stream(file.getInputStream(), file.getSize(), -1L)
                    .contentType(file.getContentType())
                    .build()
            );
            return resp.etag();
        } catch (Exception e) {
            log.error("Minio文件上传失败(fileName={})", name);
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取文件url（带过期时间与token）
     *
     * @param name 文件名
     * @return url
     */
    public String url(String name) {
        try {
            return client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(properties.getBucket())
                            .object(name)
                            .expiry(30, TimeUnit.SECONDS)
                            .build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            log.error("Minio文件URL获取失败(fileName={})", name);
            throw new RuntimeException(e);
        }
    }


}
