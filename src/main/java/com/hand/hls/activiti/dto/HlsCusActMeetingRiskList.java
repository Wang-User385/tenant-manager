package com.hand.hls.activiti.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @Author: qixiang.shao
 * @Description: 上会评审风险防范措施DTO
 * @Date: Created in 11:37 2017/12/22
 * @Modified By:
 */
@SuppressWarnings("serial")
@ExtensionAttribute(disable = true)
@Table(name = "act_meeting_risk_list")
public class HlsCusActMeetingRiskList extends BaseDTO {

    @Id
    @GeneratedValue
    private Long meetingRiskListId;

    private String meetingRiskCategory;

    private String meetingRiskType;

    private String meetingRiskDescription;

    private Long processInstanceId;

    private String selectedFlag; // add by sqx 2017.12.28 是否选择的标志

    private Long seqNumber;// add by sqx 2018.01.25 序号

    @Transient
    private String meetingRiskCategoryDesc;
    @Transient
    private String lastUpdatedDate;

    @Transient
    private String lastUpdatedPerson;

    private String riskStatus;
    @Transient
    private String statusDesc;


    public Long getMeetingRiskListId() {
        return meetingRiskListId;
    }

    public void setMeetingRiskListId(Long meetingRiskListId) {
        this.meetingRiskListId = meetingRiskListId;
    }

    public String getMeetingRiskCategory() {
        return meetingRiskCategory;
    }

    public void setMeetingRiskCategory(String meetingRiskCategory) {
        this.meetingRiskCategory = meetingRiskCategory;
    }

    public String getMeetingRiskType() {
        return meetingRiskType;
    }

    public void setMeetingRiskType(String meetingRiskType) {
        this.meetingRiskType = meetingRiskType;
    }

    public String getMeetingRiskDescription() {
        return meetingRiskDescription;
    }

    public void setMeetingRiskDescription(String meetingRiskDescription) {
        this.meetingRiskDescription = meetingRiskDescription;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(String selectedFlag) {
        this.selectedFlag = selectedFlag;
    }

    public Long getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(Long seqNumber) {
        this.seqNumber = seqNumber;
    }

    @Transient
    private Long documentId;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getMeetingRiskCategoryDesc() {
        return meetingRiskCategoryDesc;
    }

    public void setMeetingRiskCategoryDesc(String meetingRiskCategoryDesc) {
        this.meetingRiskCategoryDesc = meetingRiskCategoryDesc;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getLastUpdatedPerson() {
        return lastUpdatedPerson;
    }

    public void setLastUpdatedPerson(String lastUpdatedPerson) {
        this.lastUpdatedPerson = lastUpdatedPerson;
    }

    public String getRiskStatus() {
        return riskStatus;
    }

    public void setRiskStatus(String riskStatus) {
        this.riskStatus = riskStatus;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
}
