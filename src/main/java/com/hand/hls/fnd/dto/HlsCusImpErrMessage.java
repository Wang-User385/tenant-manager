package com.hand.hls.fnd.dto;

/**
 * Created by 王也 on 2017/7/27.
 * 导入时的错误信息
 */
public class HlsCusImpErrMessage {
    public static final String WARNING="warning";
    public static final String ERROR="error";
    public static final String TYPE_MATCH_ERROR="type error";
    public static final String NOT_NULL="not null";

    private static String split=" ";

    private String errLevel;//错误级别
    private String segmentDesc;//错误的列
    private String message;//错误信息

    public HlsCusImpErrMessage(String errLevel, String segmentDesc, String message) {
        this.errLevel = errLevel;
        this.segmentDesc = segmentDesc;
        this.message = message;
    }

    public static void setSplit(String split){
        HlsCusImpErrMessage.split=split;
    }

    public String getMessage(){
        StringBuffer sb=new StringBuffer(errLevel);
        sb.append(":");
        sb.append(segmentDesc);
        sb.append(split);
        sb.append(message);
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}
