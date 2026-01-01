package com.zijie1024.common.utils.code.img.model.enume;

/**
 * @author 字节幺零二四
 * @date 2024-03-24 16:21
 * @description ImgType
 */
public enum ImgType implements BaseEnum {
    PNG(0), GIF(1);

    private final Integer code;

    ImgType(Integer code) {
        this.code = code;
    }

    @Override
    public Integer value() {
        return this.code;
    }
}
