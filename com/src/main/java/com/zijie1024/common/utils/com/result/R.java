package com.zijie1024.common.utils.com.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 字节幺零二四
 * @date 2024-08-18 16:03
 * @description 统一返回结果
 */
@Data
@Accessors(chain = true)
public class R<T> {

    private Integer code;
    private String msg;
    private T data;

    @JsonGetter(value = "isSuccess")
    public boolean isSuccess() {
        return CommonResultEnum.SYSTEM_SUCCESS.getCode().equals(code);
    }

    private static <T> R<T> of(Integer code, String msg, T data) {
        return new R<T>().setCode(code).setMsg(msg).setData(data);
    }

    public static <T> R<T> of(ResultEnum re, T data) {
        return of(re.getCode(), re.getMsg(), data);
    }

    public static <T> R<T> ok(T data) {
        return of(CommonResultEnum.SYSTEM_SUCCESS, data);
    }

    public static <T> R<T> ok() {
        return ok(null);
    }
}
