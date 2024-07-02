package com.hand.hls.prj.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "prj_project_approval")
@Getter
@Setter
public class PrjProjectApproval extends BaseDTO {
    @Id
    @Column(name = "APPROVAL_ID")
    @GeneratedValue
    private Long approvalId;

    @Column(name = "PROJECT_ID")
    private String projectId;

    @Column(name = "MEETING_ID")
    private Long meetingId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "APPROVAL_RESULT")
    private String approvalResult;

    @Column(name = "APPROVAL_COUNT")
    private Long approvalCount;

    @Column(name = "EXISTS_COMMENT_FLAG")
    private String existsCommentFlag;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Column(name = "LAST_UPDATED_BY")
    private Long lastUpdatedBy;

    @Column(name = "LAST_UPDATE_DATE")
    private Date lastUpdateDate;

    @Column(name = "REF_N01")
    private Long refN01;

    @Column(name = "REF_N02")
    private Long refN02;

    @Column(name = "REF_N03")
    private Long refN03;

    @Column(name = "REF_N04")
    private Long refN04;

    @Column(name = "REF_N05")
    private Long refN05;

    @Column(name = "REF_N06")
    private Long refN06;

    @Column(name = "REF_N07")
    private Long refN07;

    @Column(name = "REF_N08")
    private Long refN08;

    @Column(name = "REF_N09")
    private Long refN09;

    @Column(name = "REF_N10")
    private Long refN10;

    @Column(name = "REF_D01")
    private Date refD01;

    @Column(name = "REF_D02")
    private Date refD02;

    @Column(name = "REF_D03")
    private Date refD03;

    @Column(name = "REF_D04")
    private Date refD04;

    @Column(name = "REF_D05")
    private Date refD05;

    @Column(name = "REF_D06")
    private Date refD06;

    @Column(name = "REF_D07")
    private Date refD07;

    @Column(name = "REF_D08")
    private Date refD08;

    @Column(name = "REF_D09")
    private Date refD09;

    @Column(name = "REF_D10")
    private Date refD10;

    @Column(name = "MANAGER_APPROVAL_RESULT")
    private String managerApprovalResult;

    @Column(name = "DIRECTOR_APPROVAL_RESULT")
    private String directorApprovalResult;

    @Column(name = "DIRECTOR_APPROVAL_DATE")
    private Date directorApprovalDate;

    @Column(name = "NORMAL_FLAG")
    private String normalFlag;

    @Column(name = "APPROVE_CRITERIA")
    private String approveCriteria;

    @Column(name = "MEETING_DATE")
    private Date meetingDate;

    @Column(name = "MEETING_COUNT")
    private String meetingCount;

    @Column(name = "APPROVAL_FLAG")
    private String approvalFlag;

    @Column(name = "SECOND_FLAG")
    private String secondFlag;

    @Column(name = "RECON_FLAG")
    private String reconFlag;

    @Column(name = "MEETING_TIME")
    private String meetingTime;

    @Column(name = "REF_V01")
    private String refV01;

    @Column(name = "REF_V02")
    private String refV02;

    @Column(name = "REF_V03")
    private String refV03;

    @Column(name = "REF_V04")
    private String refV04;

    @Column(name = "REF_V05")
    private String refV05;

    @Column(name = "REF_V06")
    private String refV06;

    @Column(name = "REF_V07")
    private String refV07;

    @Column(name = "REF_V08")
    private String refV08;

    @Column(name = "REF_V09")
    private String refV09;

    @Column(name = "REF_V10")
    private String refV10;

    @Column(name = "MANAGER_APPROVAL_COMMENT")
    private String managerApprovalComment;

    @Column(name = "DIRECTOR_APPROVAL_COMMENT")
    private String directorApprovalComment;

    @Column(name = "PROJECT_MANAGER_REPLAY")
    private String projectManagerReplay;

    @Column(name = "APPROVER_COMMENT_SUMMARY")
    private String approverCommentSummary;

    @Column(name = "MEETING_LOCATION")
    private String meetingLocation;

    @Transient
    private String tenantIdN;
    @Transient
    private String lessorName;
    @Transient
    private String intRate;
    @Transient
    private String leaseTerm;
    @Transient
    private Double financeAmount;
    @Transient
    private String businessTypeN;
    private String repaymentMethod;
    private String paymentMethod;
    private String foundingMethod;
    private String depositMethod;
    private String guarMethod;
    private String chargeMethod;
    private String approvalNote;
    private String approvalTerm;
    private String approvalNumber;
    private Date approvalDate;
    private String dataClass;
    private String intRateHis;
    private String leaseTermHis;
    private Double financeAmountHis;

    private String fistMeet;
    @Transient
    private String fistMeetN;
    private String other;
    private String approvalMethod;
    @Transient
    private String approvalMethodN;
    private Long reportCompany;
    @Transient
    private String reportCompanyN;

    @Column(name = "DISCUSS")
    private String discuss;

    @Column(name = "ADJUST")
    private String adjust;

    @Transient
    private Date firstApprovalDate;

    @Transient
    private String companyName;

    private Long numberOfJudges;
    private String approvedFlag;
    @Transient
    private String approvedFlagN;

    /*上会类型*/
    private String approvalType;
}