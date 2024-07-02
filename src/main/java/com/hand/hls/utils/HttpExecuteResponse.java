package com.hand.hls.utils;

import org.apache.http.Header;
import org.apache.http.HttpStatus;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class HttpExecuteResponse {
    /**
     * http 响应码
     */
    private int responseCode = -1;


    /**
     * http 执行异常
     */
    private Throwable throwable;


    /**
     * http 执行异常消息
     */
    private String errorMessage;


    /**
     * http 响应结果
     */
    private byte[] responseBody;

    /**
     * http 响应头
     */
    private Header[] headers;


    public HttpExecuteResponse() {
    }

    public HttpExecuteResponse(int responseCode, byte[] responseBody) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }

    public HttpExecuteResponse(int responseCode, Throwable throwable, String errorMessage) {
        this.responseCode = responseCode;
        this.throwable = throwable;
        this.errorMessage = errorMessage;
    }

    /**
     * http request 是否执行成功
     *
     * @return
     */
    public boolean isSuccess() {
        return responseCode == HttpStatus.SC_OK;
    }


    /**
     * 获取 http 响应结果,执行失败返回空字符串
     *
     * @return
     */
    public String getResponseAsString() {
        String response = "";
        if (responseBody == null || responseBody.length == 0) {
            return response;
        }
        try {
            response = new String(responseBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "HttpExecuteResponse{" + "responseCode=" + responseCode +
                ", throwable=" + throwable +
                ", errorMessage='" + errorMessage + '\'' +
                ", responseBody=" + getResponseAsString() +
                '}';
    }
}
