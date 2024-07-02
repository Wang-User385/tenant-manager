package com.hand.hls.app.utils;

public enum HLsCashFlowAyncEnum {

    //成功
    SUCCESS(0,"成功"),
    //新增参数为空
    NULL(4,"参数为空"),
    Token1(401,"token非法或未授权"),
    Token2(3,"token校验错误或过期"),
    //    参数验证
    DataFaild(6,"参数格式不正确");

    HLsCashFlowAyncEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
