package com.zijie1024.common.utils.code.behavior.config;

import com.zijie1024.common.utils.code.behavior.model.enume.SceneType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 字节幺零二四
 * @date 2024-05-09 14:40
 * @description BehaviorCodeProperties
 */
@Data
@Component
@ConfigurationProperties(prefix = "mine.code.behavior.ali")
public class BehaviorCodeProperties {
    /**
     * 请求地址
     */
    private String endpoint;
    /**
     * 连接超时
     */
    private int connectTimeout;
    /**
     * 读超时
     */
    private int readTimeout;
    /**
     * id
     */
    private String accessKeyId;
    /**
     * secret
     */
    private String accessKeySecret;
    /**
     * 场景ID
     */
    private Map<String, String> sceneIdMap;

    /**
     * 获取场景ID
     *
     * @param scene 场景
     * @return 场景ID
     */
    public String geSceneId(SceneType scene) {
        String k = scene.name().replace("_", "-").toLowerCase();
        return sceneIdMap.get(k);
    }
}
