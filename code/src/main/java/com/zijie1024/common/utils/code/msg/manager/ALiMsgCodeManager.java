package com.zijie1024.common.utils.code.msg.manager;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.zijie1024.common.utils.code.msg.util.MsgCodeUtil;
import darabonba.core.client.ClientOverrideConfiguration;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author 字节幺零二四
 * @date 2024-03-24 18:22
 * @description ALiMsgCodeManager
 */
@Component
public class ALiMsgCodeManager {

    @Value("${mine.code.msg.phone.ali.endpoint}")
    private String endpoint;

    @Value("${mine.code.msg.phone.ali.sign-name}")
    private String signName;

    @Value("${mine.code.msg.phone.ali.template-code}")
    private String templateCode;

    @Value("${mine.code.msg.phone.ali.access-key-id}")
    private String accessKeyId;

    @Value("${mine.code.msg.phone.ali.access-key-secret}")
    private String accessKeySecret;

    private AsyncClient client;

    private final ObjectMapper mapper = new ObjectMapper();


    @PostConstruct
    private void init() {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());
        this.client = AsyncClient.builder()
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride(this.endpoint)
                )
                .build();
    }

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    public void sendMsgCode(String phone, String code) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("code", code);
            SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                    .signName(this.signName)
                    .templateCode(this.templateCode)
                    .templateParam(mapper.writeValueAsString(params))
                    .phoneNumbers(phone)
                    .build();
            CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
            SendSmsResponse resp = response.get();
            assert "OK".equals(resp.getBody().getCode());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to send Alibaba Cloud verification code (phone=" + phone + ")");
        }
    }

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return 验证码
     */
    public String sendMsgCode(String phone) {
        String code = MsgCodeUtil.msgCode();
        sendMsgCode(phone, code);
        return code;
    }
}
