package com.hand.hls.activiti.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 工作流上会评审成员DTO
 */
@ExtensionAttribute(disable = true)
@Table(name = "act_online_meeting_member")
public class HlsCusActOnlineMeetingMember extends BaseDTO {

    @Id
    @GeneratedValue
    private Long onlineMeetingMemberId;

    private String onlineMeetingMemberCode;

    private Long processInstanceId;// 对应工作流定义ID

    private String projectDocumentCategory;//单据类型

    private Long projectId;//单据ID

    private Long allocationId;

    @Transient
    private String employeeCodeString;// 用于存储前台传来的员工code字符串

    private Long meetingId;// 用于存储上会信息Id

    @Transient
    private String memberName; // add by sqx 2018.01.09 员工姓名

    public Long getOnlineMeetingMemberId() {
        return onlineMeetingMemberId;
    }

    public void setOnlineMeetingMemberId(Long onlineMeetingMemberId) {
        this.onlineMeetingMemberId = onlineMeetingMemberId;
    }

    public String getOnlineMeetingMemberCode() {
        return onlineMeetingMemberCode;
    }

    public void setOnlineMeetingMemberCode(String onlineMeetingMemberCode) {
        this.onlineMeetingMemberCode = onlineMeetingMemberCode;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
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

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getProjectDocumentCategory() {
        return projectDocumentCategory;
    }

    public void setProjectDocumentCategory(String projectDocumentCategory) {
        this.projectDocumentCategory = projectDocumentCategory;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }


    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }
}
