package com.zijie1024.common.utils.code.img.model.enume;

/**
 * @author 字节幺零二四
 * @date 2024-03-24 16:23
 * @description CharType
 */
public enum CharType implements BaseEnum {
    NUM(0),
    CHAR(1),
    CHAR_UP(2),
    CHAR_LOW(3),
    NUM_CHAR(4),
    NUM_CHAR_UP(5),
    CHINESE(6),
    ARITHMETIC(7);

    private final Integer code;

    CharType(Integer code) {
        this.code = code;
    }

    @Override
    public Integer value() {
        return this.code;
    }
}
