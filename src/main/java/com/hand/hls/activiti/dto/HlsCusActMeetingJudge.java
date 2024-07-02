package com.hand.hls.activiti.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @Author: qixiang.shao
 * @Description: 上会评审信息表
 * @Date: Created in 19:06 2017/12/17
 * @Modified By:
 */
@SuppressWarnings("serial")
@ExtensionAttribute(disable = true)
@Table(name = "act_meeting_judge")
public class HlsCusActMeetingJudge extends BaseDTO {


    @Id
    @GeneratedValue
    private Long judgeId;

    private String judgeMemberCode;

    private String judgeSuggestion;

    private String judgeComment;

    private Long processInstanceId;

    private Long meetingId;

    private Long allocationId;

    @Transient
    private Long companyId;

    @Transient
    private String judgeMemberName;

    @Transient
    private Date judgeDate;

    @Transient
    private String name;

    @Transient
    private String positionName;

    @Transient
    private String unitName;

    @Transient
    private String companyShortName;

    @Transient
    private Date judgeTime;

    public Date getJudgeTime() {
        return judgeTime;
    }

    public void setJudgeTime(Date judgeTime) {
        this.judgeTime = judgeTime;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getCompanyShortName() {
        return companyShortName;
    }

    public void setCompanyShortName(String companyShortName) {
        this.companyShortName = companyShortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    private String toBeSatisfiedCondition; // add by sqx 2017.12.26 待满足条件

    public Long getJudgeId() {
        return judgeId;
    }

    public void setJudgeId(Long judgeId) {
        this.judgeId = judgeId;
    }

    public String getJudgeMemberCode() {
        return judgeMemberCode;
    }

    public void setJudgeMemberCode(String judgeMemberCode) {
        this.judgeMemberCode = judgeMemberCode;
    }

    public String getJudgeSuggestion() {
        return judgeSuggestion;
    }

    public void setJudgeSuggestion(String judgeSuggestion) {
        this.judgeSuggestion = judgeSuggestion;
    }

    public String getJudgeComment() {
        return judgeComment;
    }

    public void setJudgeComment(String judgeComment) {
        this.judgeComment = judgeComment;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public String getJudgeMemberName() {
        return judgeMemberName;
    }

    public void setJudgeMemberName(String judgeMemberName) {
        this.judgeMemberName = judgeMemberName;
    }

    public Date getJudgeDate() {
        return judgeDate;
    }

    public void setJudgeDate(Date judgeDate) {
        this.judgeDate = judgeDate;
    }

    public String getToBeSatisfiedCondition() {
        return toBeSatisfiedCondition;
    }

    public void setToBeSatisfiedCondition(String toBeSatisfiedCondition) {
        this.toBeSatisfiedCondition = toBeSatisfiedCondition;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }
}
