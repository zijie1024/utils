package com.zijie1024.common.utils.code.msg.example;

import com.zijie1024.common.utils.code.msg.manager.MsgCodeManager;
import com.zijie1024.common.utils.code.msg.model.enume.MsgCodeType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 字节幺零二四
 * @date 2024-03-25 9:24
 * @description MsgCodeController
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("util/code/msg")
public class MsgCodeController {

    private final MsgCodeManager msgCodeManager;

    @GetMapping("email/qq")
    public String sendQQEmailCode() {
        long t = System.currentTimeMillis();
        String email = "3319105578@qq.com";
        String code = msgCodeManager.sendCodeAsync(
                MsgCodeType.EMAIL_QQ,
                email
        );
        System.out.println("sendQQEmailCode: time=" + (System.currentTimeMillis() - t));
        return code;
    }

    @GetMapping("email/qq/ret")
    public String sendQQEmailCodeRet() {
        long t = System.currentTimeMillis();
        String email = "3319105578@qq.com";
        String code = msgCodeManager.sendCode(
                MsgCodeType.EMAIL_QQ,
                email
        );
        System.out.println("sendQQEmailCodeRet: time=" + (System.currentTimeMillis() - t));
        return code;
    }


    @GetMapping("phone/ali")
    public String sendALiPhoneCode() {
        long t = System.currentTimeMillis();
        String phone = "15727601308";
        String code = msgCodeManager.sendCodeAsync(
                MsgCodeType.PHONE_ALI,
                phone
        );
        System.out.println("sendALiPhoneCode: time=" + (System.currentTimeMillis() - t));
        return code;
    }

    @GetMapping("phone/ali/ret")
    public String sendALiPhoneCodeRet() {
        long t = System.currentTimeMillis();
        String phone = "15727601308";
        String code = msgCodeManager.sendCode(
                MsgCodeType.PHONE_ALI,
                phone
        );
        System.out.println("sendALiPhoneCodeRet: time=" + (System.currentTimeMillis() - t));
        return code;
    }
}
