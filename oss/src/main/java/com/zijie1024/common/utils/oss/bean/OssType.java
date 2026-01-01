package com.zijie1024.common.utils.oss.bean;

import lombok.Getter;

/**
 * @author 字节幺零二四
 * @date 2025-02-15 20:47
 * @description OssType
 */
@Getter
public enum OssType {
    MINIO(0),
    ALI(1),
    QI_NIU(2);
    private final Integer code;

    OssType(Integer code) {
        this.code = code;
    }
}
