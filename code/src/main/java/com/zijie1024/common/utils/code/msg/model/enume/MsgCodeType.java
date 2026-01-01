package com.zijie1024.common.utils.code.msg.model.enume;

/**
 * @author 字节幺零二四
 * @date 2024-03-24 18:50
 * @description MsgCodeType
 */
public enum MsgCodeType implements BaseEnum {
    PHONE_ALI(0),
    EMAIL_QQ(1);

    private final Integer code;

    MsgCodeType(Integer code) {
        this.code = code;
    }

    @Override
    public Integer value() {
        return this.code;
    }
}
