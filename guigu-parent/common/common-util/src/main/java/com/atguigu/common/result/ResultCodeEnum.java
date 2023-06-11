package com.atguigu.common.result;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),
    DATA_ERROR(204, "数据异常"),

    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),
    LOGIN_MOBLE_ERROR(210, "用户名或密码错误"),
    ACCOUNT_STOP(211, "账号已停用");

    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
