package com.zijie1024.common.utils.oss;

import com.zijie1024.common.utils.oss.bean.OssType;
import com.zijie1024.common.utils.oss.minio.MinioManager;
import com.zijie1024.common.utils.oss.qiniu.QiNiuManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 字节幺零二四
 * @date 2025-02-15 20:47
 * @description OssManager
 */
@Component
@RequiredArgsConstructor
public class OssManager {

    private final MinioManager minioManager;
    private final QiNiuManager qiNiuManager;

    /**
     * 上传文件：使用默认文件上传方式
     *
     * @param file 文件
     * @param name 文件名（含路径）
     * @return 文件hash值
     */
    public String upload(MultipartFile file, String name) {
        return upload(file, name, OssType.MINIO);
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @param name 文件名（含路径）
     * @param type 文件上传方式
     * @return 文件hash值
     */
    public String upload(MultipartFile file, String name, OssType type) {
        return switch (type) {
            case MINIO -> minioManager.upload(file, name);
            case QI_NIU -> qiNiuManager.upload(file, name);
            default -> throw new UnsupportedOperationException("The oss platform is not supported");
        };
    }

}
