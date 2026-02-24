package com.zijie1024.common.utils.com.result;

/**
 * @author 字节幺零二四
 * @date 2024-08-18 16:05
 * @description 通用返回结果类型枚举
 */
public enum CommonResultEnum implements ResultEnum {
    SYSTEM_SUCCESS(4_00_00_00, "处理成功"),
    SYSTEM_ERROR(4_00_00_01, "系统异常"),
    PARAM_ERROR(4_00_00_02, "参数错误"),
    NETWORK_ERROR(4_00_00_03, "网络异常"),
    ENVIRONMENT_ABNORMAL(4_00_00_04, "操作环境异常,请刷新页面后重试"),
    LONG_TIME_NO_OPERATION(4_00_00_05, "长时间未操作,请刷新页面后重试"),
    OPERATION_NOT_SUPPORTED(4_00_00_06, "操作不支持"),

    CODE_BEHAVIOR_ERROR(4_00_01_01, "行为验证码校验失败"),
    CODE_MSG_PHONE_SEND_ERROR(4_00_01_02, "短信验证码发送失败"),
    CODE_MSG_PHONE_ERROR_OR_EXPIRED(4_00_01_03, "短信验证码不正确或已过期"),
    CODE_MSG_EMAIL_SEND_ERROR(4_00_01_04, "邮箱验证码发送失败"),
    CODE_MSG_EMAIL_ERROR_OR_EXPIRED(4_00_01_05, "邮箱验证码发送失败"),

    MEMBER_TOKEN_LACK(4_00_02_01, "用户未登录"),
    MEMBER_TOKEN_ERROR_OR_EXPIRED(4_00_02_02, "登录已过期,请重新登录"),
    MEMBER_STATE_DISABLE(4_00_02_03, "用户已被禁用"),
    MEMBER_STATE_CANCELED(4_00_02_04, "用户已注销");

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    CommonResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final Integer code;
    private final String msg;
}
