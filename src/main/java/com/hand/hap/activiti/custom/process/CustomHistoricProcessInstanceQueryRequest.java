//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hap.activiti.custom.process;

import org.activiti.rest.service.api.history.HistoricProcessInstanceQueryRequest;

public class CustomHistoricProcessInstanceQueryRequest extends HistoricProcessInstanceQueryRequest {
    private String processDefinitionNameLike;
    private String startUserName;
    private Boolean suspended;
    private String carbonCopyUser;
    private String readFlag;
    private String documentName;

    public CustomHistoricProcessInstanceQueryRequest() {
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getProcessDefinitionNameLike() {
        return this.processDefinitionNameLike;
    }

    public void setProcessDefinitionNameLike(String processDefinitionNameLike) {
        this.processDefinitionNameLike = processDefinitionNameLike;
    }

    public String getStartUserName() {
        return this.startUserName;
    }

    public void setStartUserName(String startUserName) {
        this.startUserName = startUserName;
    }

    public Boolean isSuspended() {
        return this.suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }

    public String getCarbonCopyUser() {
        return this.carbonCopyUser;
    }

    public void setCarbonCopyUser(String carbonCopyUser) {
        this.carbonCopyUser = carbonCopyUser;
    }

    public String getReadFlag() {
        return this.readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }
}
