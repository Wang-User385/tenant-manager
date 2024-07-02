package com.hand.hls.activiti.dto;

import com.hand.hap.core.annotation.Children;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description: 工作流上会信息DTO
 * @Date: Created in 16:16 2017/12/17
 * @Modified By:
 */
@SuppressWarnings("serial")
@ExtensionAttribute(disable = true)
@Table(name = "act_meeting")
public class HlsCusActMeeting extends BaseDTO {

    @Id
    @GeneratedValue
    private Long meetingId;

    private Long documentId;

    private String documentCategory;

    private String meetingName;

    private String meetingLocation;

    private Date meetingDate;

    private String meetingTime;

    private Long numberOfJudges;

    private String votingResult;

    private String meetingSummary;

    private String suppleCondition;

    private String meetingRecord;

    private Long processInstanceId;

    private String meetingType;

    private String comprehensiveComment; // add by sqx 2017.12.27 综合意见

    @Transient
    private String meetingRecordName; // add by sqx 2018.01.11 会议记录人名字

    @Transient
    private String employeeCodeString;

    @Transient
    @Children
    private List<HlsCusActOnlineMeetingMember> hlsCusActOnlineMeetingMemberList;

    public List<HlsCusActOnlineMeetingMember> getHlsCusActOnlineMeetingMemberList() {
        return hlsCusActOnlineMeetingMemberList;
    }

    public void setHlsCusActOnlineMeetingMemberList(List<HlsCusActOnlineMeetingMember> hlsCusActOnlineMeetingMemberList) {
        this.hlsCusActOnlineMeetingMemberList = hlsCusActOnlineMeetingMemberList;
    }

    public String getEmployeeCodeString() {
        return employeeCodeString;
    }

    public void setEmployeeCodeString(String employeeCodeString) {
        this.employeeCodeString = employeeCodeString;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentCategory() {
        return documentCategory;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = meetingLocation;
    }

    public Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Date meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public Long getNumberOfJudges() {
        return numberOfJudges;
    }

    public void setNumberOfJudges(Long numberOfJudges) {
        this.numberOfJudges = numberOfJudges;
    }

    public String getVotingResult() {
        return votingResult;
    }

    public void setVotingResult(String votingResult) {
        this.votingResult = votingResult;
    }

    public String getMeetingSummary() {
        return meetingSummary;
    }

    public void setMeetingSummary(String meetingSummary) {
        this.meetingSummary = meetingSummary;
    }

    public String getSuppleCondition() {
        return suppleCondition;
    }

    public void setSuppleCondition(String suppleCondition) {
        this.suppleCondition = suppleCondition;
    }

    public String getMeetingRecord() {
        return meetingRecord;
    }

    public void setMeetingRecord(String meetingRecord) {
        this.meetingRecord = meetingRecord;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getComprehensiveComment() {
        return comprehensiveComment;
    }

    public void setComprehensiveComment(String comprehensiveComment) {
        this.comprehensiveComment = comprehensiveComment;
    }

    public String getMeetingRecordName() {
        return meetingRecordName;
    }

    public void setMeetingRecordName(String meetingRecordName) {
        this.meetingRecordName = meetingRecordName;
    }
}
