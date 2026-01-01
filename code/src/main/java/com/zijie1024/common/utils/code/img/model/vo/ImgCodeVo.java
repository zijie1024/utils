package com.zijie1024.common.utils.code.img.model.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 字节幺零二四
 * @date 2024-03-24 16:21
 * @description ImgCodeVo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImgCodeVo<T> {
    /**
     * 验证码值
     */
    private String code;
    /**
     * 验证码图片数据
     */
    private T data;
}
