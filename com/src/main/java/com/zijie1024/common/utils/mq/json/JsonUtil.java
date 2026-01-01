package com.zijie1024.common.utils.mq.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author 字节幺零二四
 * @date 2026-01-01 13:14
 * @description Json类型的序列化与反序列化
 */
public final class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 支持 java.time
        objectMapper.registerModule(new JavaTimeModule());
    }

    private JsonUtil() {
    }

    /**
     * 将Java对象转换为Json字符串
     *
     * @param obj Java对象
     * @param <T> 源类
     * @return Json字符串
     */
    public static <T> String stringify(T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将Json字符串转换为Java对象
     *
     * @param js    Json字符串
     * @param clazz Java类型
     * @param <T>   目标类
     * @return 如果jsonStr不为null，返回Java对象，否则返回null
     */
    public static <T> T parse(String js, Class<T> clazz) {
        try {
            return objectMapper.readValue(js, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将Json字符串转换为JsonNode对象
     *
     * @param content Json字符串
     * @return JsonNode对象
     */
    public static JsonNode readTree(String content) {
        try {
            return objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
