package com.zijie1024.common.utils.code.msg.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 字节幺零二四
 * @date 2024-03-24 18:34
 * @description MsgCodeUtil
 */
@Component
public class MsgCodeUtil {

    /**
     * 消息验证码长度
     */
    public static int length;

    /**
     * 注入消息验证码长度
     */
    @Value("${mine.code.msg.all.length}")
    public void setLength(int length) {
        MsgCodeUtil.length = length;
    }


    /**
     * 生成消息验证码
     *
     * @return 消息验证码
     */
    public static String msgCode() {
        return randomIntStr(length);
    }

    /**
     * 随机生成指定长度的整数序列
     *
     * @param bit 位数/长度
     * @return 整数序列
     */
    public static String randomIntStr(int bit) {
        StringBuilder builder = new StringBuilder();
        while (bit-- != 0) builder.append(randomInt(0, 9));
        return builder.toString();
    }


    /**
     * 随机生成指定范围的整数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机整数
     */
    private static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
