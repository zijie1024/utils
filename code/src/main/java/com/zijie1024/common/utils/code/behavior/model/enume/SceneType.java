package com.zijie1024.common.utils.code.behavior.model.enume;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author 字节幺零二四
 * @date 2024-03-24 17:16
 * @description 场景类型
 */
public enum SceneType {

    LOGIN(0),
    REGISTER(1),
    CENTER(2),
    FIND_PASSWORD(3);

    @JsonValue
    private final Integer code;

    SceneType(Integer code) {
        this.code = code;
    }

}
