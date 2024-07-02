//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.dto;

public class RequestHead {
    private String transactionNo = "";
    private String organizationCode = "";
    private String operatorCode = "";
    private String channelCode = "";
    private String remark = "";

    public RequestHead() {
    }

    public String getTransactionNo() {
        return this.transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getOrganizationCode() {
        return this.organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getOperatorCode() {
        return this.operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getChannelCode() {
        return this.channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setBasicInfo(String transactionNo, String organizationCode) {
        this.transactionNo = transactionNo;
        this.organizationCode = organizationCode;
    }

    public void setBasicInfo(String transactionNo, String organizationCode, String operatorCode) {
        this.transactionNo = transactionNo;
        this.organizationCode = organizationCode;
        this.operatorCode = operatorCode;
    }

    public void setBasicInfo(String transactionNo, String organizationCode, String operatorCode, String channelCode) {
        this.transactionNo = transactionNo;
        this.organizationCode = organizationCode;
        this.operatorCode = operatorCode;
        this.channelCode = channelCode;
    }

    public void setBasicInfo(String transactionNo, String organizationCode, String operatorCode, String channelCode, String remark) {
        this.transactionNo = transactionNo;
        this.organizationCode = organizationCode;
        this.operatorCode = operatorCode;
        this.channelCode = channelCode;
        this.remark = remark;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("RequestHead");
        return stringBuffer.append(" [transactionNo=").append(this.transactionNo).append(", organizationCode=").append(this.organizationCode).append(", operatorCode=").append(this.operatorCode).append(", channelCode=").append(this.channelCode).append(", remark=").append(this.remark + "]").toString();
    }
}
