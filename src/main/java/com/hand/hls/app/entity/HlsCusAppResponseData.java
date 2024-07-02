package com.hand.hls.app.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;

/**
 * @Author:
 * @Description: App接口返回对象
 * @Modified By:
 */
//@JsonIgnoreProperties(value = { "data" })
//@ExtensionAttribute
public class HlsCusAppResponseData implements Serializable {


    private static final long serialVersionUID = -7559477629891176110L;
    private Boolean success;

    private String sourceId;

    private String docmentNum;

    private String returnStatus;

    private String returnCode;

    private String returnMsg;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }


    public String getDocmentNum() {
        return docmentNum;
    }

    public void setDocmentNum(String docmentNum) {
        this.docmentNum = docmentNum;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public String toString() {
        return "HlsCusAppResponseData{" +
                "success=" + success +
                ", sourceId='" + sourceId + '\'' +
                ", docmentNum='" + docmentNum + '\'' +
                ", returnStatus='" + returnStatus + '\'' +
                ", returnCode='" + returnCode + '\'' +
                ", returnMsg='" + returnMsg + '\'' +
                '}';
    }
}
