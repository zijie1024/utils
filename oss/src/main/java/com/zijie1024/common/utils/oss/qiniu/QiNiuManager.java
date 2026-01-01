package com.zijie1024.common.utils.oss.qiniu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author 字节幺零二四
 * @date 2025-02-15 20:47
 * @description QiNiuManager
 */
@Slf4j
@Component
public class QiNiuManager {

    private final QiNiuProperties properties;
    private final UploadManager uploadManager;
    private final Auth auth;
    private final ObjectMapper om = new ObjectMapper();

    public QiNiuManager(QiNiuProperties properties) {
        this.properties = properties;
        Configuration config = new Configuration(Region.qvmHuadong());
        config.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
        this.uploadManager = new UploadManager(new Configuration());
        this.auth = Auth.create(properties.getAccessKey(), properties.getSecretKey());
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @param name 文件名，若不指定，则采用文件hash值作为文件名
     * @return 文件hash
     */
    public String upload(MultipartFile file, String name) {
        String token = auth.uploadToken(properties.getBucket());
        try {
            Response resp = uploadManager.put(
                    file.getBytes(),
                    name,
                    token
            );
            DefaultPutRet dpr = om.readValue(resp.bodyString(), DefaultPutRet.class);
            return dpr.hash;
        } catch (IOException e) {
            String msg = "File upload failed, name=" + name;
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * 获取文件url（带过期时间与token）
     *
     * @param name 文件名
     * @return url
     */
    public String url(String name) {
        name = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
        String url = String.format("%s/%s", properties.getDomain(), name);
        return auth.privateDownloadUrl(url, properties.getImageAccessExpirationTime());
    }


}
