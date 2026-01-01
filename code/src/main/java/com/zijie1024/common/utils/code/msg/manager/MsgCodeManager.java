package com.zijie1024.common.utils.code.msg.manager;



import com.zijie1024.common.utils.code.msg.model.enume.MsgCodeType;
import com.zijie1024.common.utils.code.msg.util.MsgCodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author 字节幺零二四
 * @date 2024-03-24 18:52
 * @description MsgCodeManager
 */
@Component
@RequiredArgsConstructor
public class MsgCodeManager {

    private final ALiMsgCodeManager aLiMsgCodeManager;
    private final QQMsgCodeManager qqMsgCodeManager;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Value("${mine.code.msg.email.qq.subject}")
    private String qqSubject;

    @Value("${mine.code.msg.email.qq.template}")
    private String qqTemplate;

    /**
     * 同步发送消息验证码
     *
     * @param type    验证码类型
     * @param account 账号
     * @return 验证码
     */
    public String sendCode(MsgCodeType type, String account) {
        String code = MsgCodeUtil.msgCode();
        sendCode(type, account, code);
        return code;
    }

    /**
     * 异步发送消息验证码
     *
     * @param type    验证码类型
     * @param account 账号
     * @return 验证码
     */
    public String sendCodeAsync(MsgCodeType type, String account) {
        String code = MsgCodeUtil.msgCode();
        taskExecutor.execute(() -> sendCode(type, account, code));
        return code;
    }


    /**
     * 发送消息验证码
     *
     * @param type    验证码类型
     * @param account 账号
     * @param code    验证码
     */
    private void sendCode(MsgCodeType type, String account, String code) {
        switch (type) {
            case PHONE_ALI -> aLiMsgCodeManager.sendMsgCode(account, code);
            case EMAIL_QQ -> qqMsgCodeManager.sendEmail(
                    account,
                    qqSubject,
                    qqTemplate,
                    code
            );
            default -> throw new RuntimeException("消息验证码发送失败(account=" + account + ")");
        }
    }

}
