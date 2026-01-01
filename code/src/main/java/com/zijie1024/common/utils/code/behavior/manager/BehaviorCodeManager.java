package com.zijie1024.common.utils.code.behavior.manager;

import com.aliyun.captcha20230305.Client;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaRequest;
import com.aliyun.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import com.aliyun.teaopenapi.models.Config;
import com.zijie1024.common.utils.code.behavior.config.BehaviorCodeProperties;
import com.zijie1024.common.utils.code.behavior.model.enume.SceneType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author 字节幺零二四
 * @date 2024-03-24 17:16
 * @description BehaviorCodeManager
 */
@Component
@RequiredArgsConstructor
public class BehaviorCodeManager {


    private final BehaviorCodeProperties properties;

    private Client client;

    @PostConstruct
    private void init() throws Exception {
        Config config = new Config();
        config.accessKeyId = properties.getAccessKeyId();
        config.accessKeySecret = properties.getAccessKeySecret();
        config.endpoint = properties.getEndpoint();
        config.connectTimeout = properties.getConnectTimeout();
        config.readTimeout = properties.getReadTimeout();
        client = new Client(config);
    }

    /**
     * 校验行为验证码是否正确
     *
     * @param sceneId     场景ID
     * @param verifyParam 验证参数
     * @return 行为验证码校验结果
     */
    public boolean check(String sceneId, String verifyParam) {
        VerifyIntelligentCaptchaRequest request = new VerifyIntelligentCaptchaRequest();
        request.sceneId = sceneId;
        request.captchaVerifyParam = verifyParam;
        try {
            VerifyIntelligentCaptchaResponse resp = client.verifyIntelligentCaptcha(request);
            return resp != null && resp.body.result.verifyResult;
            // 错误码/原因码
            // String code = resp.body.result.verifyCode;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 校验行为验证码是否正确
     *
     * @param scene       场景
     * @param verifyParam 验证参数
     * @return 行为验证码校验结果
     */
    public boolean check(SceneType scene, String verifyParam) {
        VerifyIntelligentCaptchaRequest request = new VerifyIntelligentCaptchaRequest();
        request.sceneId = properties.geSceneId(scene);
        request.captchaVerifyParam = verifyParam;
        try {
            VerifyIntelligentCaptchaResponse resp = client.verifyIntelligentCaptcha(request);
            return resp != null && resp.body.result.verifyResult;
            // 错误码/原因码
            // String code = resp.body.result.verifyCode;
        } catch (Exception e) {
            return false;
        }
    }

}
