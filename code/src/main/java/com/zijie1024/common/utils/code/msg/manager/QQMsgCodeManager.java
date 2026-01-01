package com.zijie1024.common.utils.code.msg.manager;


import com.zijie1024.common.utils.code.msg.util.MsgCodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @author 字节幺零二四
 * @date 2024-03-25 8:52
 * @description QQMsgCodeManager
 */
@Component
@RequiredArgsConstructor
public class QQMsgCodeManager {

    private final JavaMailSender javaMailSender;

    @Value("${mine.code.msg.email.qq.from}")
    private String from;


    /**
     * 发送邮箱验证码
     *
     * @param to          收件人邮箱
     * @param sub         主题
     * @param msgTemplate 消息模板，必须包含%s，用于验证码占位
     * @param code        验证码
     */
    public void sendEmail(String to, String sub, String msgTemplate, String code) {
        try {
            SimpleMailMessage smm = new SimpleMailMessage();
            smm.setFrom(this.from);
            smm.setTo(to);
            smm.setSubject(sub);
            smm.setText(String.format(msgTemplate, code));
            javaMailSender.send(smm);
        } catch (Exception e) {
            throw new RuntimeException("邮箱验证码发送失败(email=" + to + ")");
        }
    }

    /**
     * 发送邮箱验证码
     *
     * @param to          收件人邮箱
     * @param sub         主题
     * @param msgTemplate 消息模板，必须包含%s，用于验证码占位
     * @return 验证码
     */
    public String sendEmail(String to, String sub, String msgTemplate) {
        String code = MsgCodeUtil.msgCode();
        sendEmail(to, sub, msgTemplate, code);
        return code;
    }
}
