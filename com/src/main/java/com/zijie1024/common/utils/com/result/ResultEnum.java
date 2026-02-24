package com.zijie1024.common.utils.com.result;

/**
 * @author 字节幺零二四
 * @date 2024-08-18 16:04
 * @description ResultEnum
 */
public interface ResultEnum {
    /**
     * 获取返回码
     *
     * @return 返回码
     */
    Integer getCode();

    /**
     * 获取返回信息
     *
     * @return 返回消息
     */
    String getMsg();
}
