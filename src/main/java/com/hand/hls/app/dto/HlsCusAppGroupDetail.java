package com.hand.hls.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Qian Yuanfeng
 * @date 2020/9/22 - 16:20
 */
@Getter
@Setter
public class HlsCusAppGroupDetail {

    private String businessKey;

    private String groupIndex;

    private String groupLabel;

    private String groupType;

    private String label;

    private String value;

    private List<HlsCusAppGroupDetail> detailList;

    private List<List> details;

    private String groupName;

    private List<Map<String,String>> bpList;

    private List<Map<String,String>> employeesList;

    private List<List> meetingDetailList;

    private List<Map<String,String>> attachmentList;
    private List<Map<String,String>> projectAttachmentList;

    private List<List> transforBaseInfoList;

    private List<List> paymentList;

    public List<List> getBpBankList() {
        return bpBankList;
    }

    public void setBpBankList(List<List> bpBankList) {
        this.bpBankList = bpBankList;
    }

    private List<List> bankList;

    private List<List> bpBankList;

    private List<List> penaltyDetailList;
    /**
     * 公司主体变更
     */
    private List<List> changeList;

    private List<List> leaseItemList;

    private List<List> mortgageList;

    private List<List> contractBpList;

    private List<List> pledgeList;


    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(String groupIndex) {
        this.groupIndex = groupIndex;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<HlsCusAppGroupDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<HlsCusAppGroupDetail> detailList) {
        this.detailList = detailList;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<List> getDetails() {
        return details;
    }

    public void setDetails(List<List> details) {
        this.details = details;
    }

    public List<Map<String, String>> getBpList() {
        return bpList;
    }

    public void setBpList(List<Map<String, String>> bpList) {
        this.bpList = bpList;
    }

    public List<Map<String, String>> getEmployeesList() {
        return employeesList;
    }

    public void setEmployeesList(List<Map<String, String>> employeesList) {
        this.employeesList = employeesList;
    }

    public List<Map<String, String>> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Map<String, String>> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public List<List> getMeetingDetailList() {
        return meetingDetailList;
    }

    public void setMeetingDetailList(List<List> meetingDetailList) {
        this.meetingDetailList = meetingDetailList;
    }

    public List<List> getTransforBaseInfoList() {
        return transforBaseInfoList;
    }

    public void setTransforBaseInfoList(List<List> transforBaseInfoList) {
        this.transforBaseInfoList = transforBaseInfoList;
    }

    public List<List> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<List> paymentList) {
        this.paymentList = paymentList;
    }

    public List<List> getBankList() {
        return bankList;
    }

    public void setBankList(List<List> bankList) {
        this.bankList = bankList;
    }

    public List<List> getPenaltyDetailList() {
        return penaltyDetailList;
    }

    public void setPenaltyDetailList(List<List> penaltyDetailList) {
        this.penaltyDetailList = penaltyDetailList;
    }
}
