package com.zijie1024.common.utils.code.img.manager;


import com.zijie1024.common.utils.code.img.model.enume.CharType;
import com.zijie1024.common.utils.code.img.model.enume.ImgType;
import com.zijie1024.common.utils.code.img.model.vo.ImgCodeVo;
import com.zijie1024.common.utils.code.img.util.EasyCaptchaUtil;
import org.springframework.stereotype.Component;

/**
 * @author 字节幺零二四
 * @date 2024-03-24 16:39
 * @description CodeManager
 */
@Component
public class CodeManager {

    /**
     * 获取图片验证码
     *
     * @param imgType  图片类型
     * @param charType 字符类型
     * @return 验证码值及验证码图片(base64)
     */
    public ImgCodeVo<String> getImgCode(ImgType imgType, CharType charType) {
        return EasyCaptchaUtil.getImgCode(imgType, charType);
    }

}
