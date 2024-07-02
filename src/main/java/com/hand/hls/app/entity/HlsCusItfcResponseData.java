package com.hand.hls.app.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @Author:
 * @Description: App接口返回对象
 * @Modified By:
 */
//@JsonIgnoreProperties(value = { "data" })
//@ExtensionAttribute
public class HlsCusItfcResponseData implements Serializable {

    private static final long serialVersionUID = -7559477629891176110L;
    private String prnd;

    private String msg;

    private int code;
    @JsonInclude
    private Object data;

    public String getPrnd() {
        return prnd;
    }

    public void setPrnd(String prnd) {
        this.prnd = prnd;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }



    @Override
    public String toString() {
        return "HlsCusItfcResponseData{" +
                "prnd=" + prnd +
                ", msg='" + msg + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }
}
