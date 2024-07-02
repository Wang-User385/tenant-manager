package com.hand.hls.cont.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@ExtensionAttribute(
        disable = true
)
@Table(name = "CON_CHANGE_REPAYMENT_INFO")
public class ConChangeRepaymentInfo extends BaseDTO {
    /**
     * ID
     */
    @Id
    @GeneratedValue
    @Column(name = "CHANGE_REPAYMENT_INFO_ID")
    private Long changeRepaymentInfoId;

    /**
     * 变更起始日
     */
    @Column(name = "CHANGE_START_DATE")
    private Date changeStartDate;

    /**
     * 变更起始期数
     */
    @Column(name = "CHANGE_START_TIMES")
    private Long changeStartTimes;

    /**
     * 变更后总期数
     */
    @Column(name = "AFTER_TOTAL_TIMES")
    private Long afterTotalTimes;

    /**
     * 变更前总期数
     */
    @Column(name = "BEFORE_TOTAL_TIMES")
    private Long beforeTotalTimes;

    /**
     * 变更后剩余期数
     */
    @Column(name = "AFTER_REMAIN_TIMES")
    private Long afterRemainTimes;

    /**
     * 还款变更方案 展期extension/缩期contraction
     */
    @Column(name = "REPAYMENT_CHANGE_TYPE")
    private String repaymentChangeType;

    @Transient
    private String repaymentChangeTypeN;

    /**
     * 变更手续费
     */
    @Column(name = "CCR_FEE")
    private Double ccrFee;

    /**
     * old_xirr
     */
    @Column(name = "OLD_XIRR")
    private Double oldXirr;

    /**
     * new_xirr
     */
    @Column(name = "NEW_XIRR")
    private Double newXirr;

    /**
     * 变更虚拟合同id(复制出来的prj_project数据)
     */
    @Column(name = "PROJECT_ID")
    private Long projectId;

    /**
     * 原虚拟合同id
     */
    @Column(name = "OLD_PROJECT_ID")
    private Long oldProjectId;

    /**
     * 合同id
     */
    @Column(name = "CONTRACT_ID")
    private Long contractId;

    /**
     * 变更头id
     */
    @Column(name = "CHANGE_REQ_ID")
    private Long changeReqId;

    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "PROGRAM_ID")
    private Long programId;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Column(name = "LAST_UPDATED_BY")
    private Long lastUpdatedBy;

    @Column(name = "LAST_UPDATE_DATE")
    private Date lastUpdateDate;

    @Column(name = "LAST_UPDATE_LOGIN")
    private Long lastUpdateLogin;

    @Column(name = "OBJECT_VERSION_NUMBER")
    private Long objectVersionNumber;

    @Column(name = "AFTER_LEASE_END_DATE")
    private Date afterLeaseEndDate;

    @Column(name = "BEFORE_LEASE_END_DATE")
    private Date beforeLeaseEndDate;

    @Column(name = "AFTER_TOTAL_AMOUNT")
    private Double afterTotalAmount;

    @Column(name = "BEFORE_TOTAL_AMOUNT")
    private Double beforeTotalAmount;

    @Column(name = "AFTER_TOTAL_PRINCIPAL")
    private Double afterTotalPrincipal;

    @Column(name = "BEFORE_TOTAL_PRINCIPAL")
    private Double beforeTotalPrincipal;

    @Column(name = "AFTER_TOTAL_INTEREST")
    private Double afterTotalInterest;

    @Column(name = "BEFORE_TOTAL_INTEREST")
    private Double beforeTotalInterest;

    @Column(name = "AFTER_TOTAL_DEPOSIT")
    private Double afterTotalDeposit;

    @Column(name = "BEFORE_TOTAL_DEPOSIT")
    private Double beforeTotalDeposit;

    @Column(name = "AFTER_TOTAL_FEE")
    private Double afterTotalFee;

    @Column(name = "BEFORE_TOTAL_FEE")
    private Double beforeTotalFee;

    @Column(name = "AFTER_IRR")
    private Double afterIrr;

    @Column(name = "BEFORE_IRR")
    private Double beforeIrr;

    @Column(name = "CALC_FLAG")
    private String calcFlag;
}