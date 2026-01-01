package com.zijie1024.common.utils.code.img.util;


import com.zijie1024.common.utils.code.img.model.constant.CaptchaConstant;
import com.zijie1024.common.utils.code.img.model.enume.CharType;
import com.zijie1024.common.utils.code.img.model.enume.ImgType;
import com.zijie1024.common.utils.code.img.model.vo.ImgCodeVo;
import io.github.eternalstone.captcha.gp.base.Captcha;
import io.github.eternalstone.captcha.gp.base.CaptchaEnum;
import io.github.eternalstone.captcha.gp.base.TextEntry;
import io.github.eternalstone.captcha.gp.factory.CaptchaFactory;
import io.github.eternalstone.captcha.gp.factory.CaptchaProperty;

/**
 * @author 字节幺零二四
 * @date 2024-03-24 16:22
 * @description EasyCaptcha工具类
 */
public class EasyCaptchaUtil {
    /**
     * 获取图片验证码
     *
     * @param imgType  图片类型
     * @param charType 字符类型
     * @return 验证码值及验证码图片(base64)
     */
    public static ImgCodeVo<String> getImgCode(ImgType imgType, CharType charType) {
        CaptchaProperty ppt = switch (imgType) {
            case PNG -> getPngCodeProperty(charType);
            case GIF -> getGifCodeProperty(charType);
        };
        Captcha captcha = CaptchaFactory.getCaptcha(ppt);
        TextEntry text = captcha.createText();
        return new ImgCodeVo<>(text.getKey(), captcha.toBase64(text));
    }

    /**
     * 获取GIF图片验证码配置
     *
     * @param charType 字符类型
     * @return 配置
     */
    private static CaptchaProperty getGifCodeProperty(CharType charType) {
        CaptchaProperty ppt = new CaptchaProperty();
        ppt.setHeight(CaptchaConstant.IMG_HEIGHT);
        ppt.setWidth(CaptchaConstant.IMG_WIDTH);
        ppt.setCaptcha(CaptchaEnum.GIF);
        switch (charType) {
            case CHINESE -> ppt.setCaptcha(CaptchaEnum.CHINESE_GIF);
            case NUM, CHAR, CHAR_LOW, CHAR_UP, NUM_CHAR, NUM_CHAR_UP ->
                    ppt.setCharType(toEasyCaptchaCharType(charType));
            default -> throw new RuntimeException("GIF字符类型设置异常");
        }
        return ppt;
    }

    /**
     * 获取PNG图片验证码配置
     *
     * @param charType 字符类型
     * @return 配置
     */
    private static CaptchaProperty getPngCodeProperty(CharType charType) {
        CaptchaProperty ppt = new CaptchaProperty();
        ppt.setHeight(CaptchaConstant.IMG_HEIGHT);
        ppt.setWidth(CaptchaConstant.IMG_WIDTH);
        ppt.setCaptcha(CaptchaEnum.SPEC);
        switch (charType) {
            case CHINESE -> ppt.setCaptcha(CaptchaEnum.CHINESE);
            case ARITHMETIC -> ppt.setCaptcha(CaptchaEnum.ARITHMETIC);
            case NUM, CHAR, CHAR_LOW, CHAR_UP, NUM_CHAR, NUM_CHAR_UP ->
                    ppt.setCharType(toEasyCaptchaCharType(charType));
            default -> throw new RuntimeException("PNG字符类型设置异常");
        }
        return ppt;
    }

    /**
     * 自定义字符类型转easy-captcha字符类型
     *
     * @param charType 自定义字符类型
     * @return easy-captcha字符类型
     */
    private static Integer toEasyCaptchaCharType(CharType charType) {
        return switch (charType) {
            case NUM -> Captcha.TYPE_ONLY_NUMBER;
            case CHAR -> Captcha.TYPE_ONLY_CHAR;
            case CHAR_UP -> Captcha.TYPE_ONLY_UPPER;
            case CHAR_LOW -> Captcha.TYPE_ONLY_LOWER;
            case NUM_CHAR -> Captcha.TYPE_DEFAULT;
            case NUM_CHAR_UP -> Captcha.TYPE_NUM_AND_UPPER;
            default -> throw new RuntimeException("字符类型转换异常");
        };
    }
}
