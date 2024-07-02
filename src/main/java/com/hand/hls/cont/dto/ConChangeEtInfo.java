package com.hand.hls.cont.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@ToString
@Table(name = "CON_CHANGE_ET_INFO")
public class ConChangeEtInfo {
    /**
     * ID
     */
    @Id
    @GeneratedValue
    @Column(name = "CHANGE_ET_INFO_ID")
    private Long changeEtInfoId;

    /**
     * 提前结清日
     */
    @Column(name = "ET_DATE")
    private Date etDate;

    /**
     * 未还本金总额
     */
    @Column(name = "UNRECEIVED_PRINCIPAL")
    private Double unreceivedPrincipal;

    /**
     * 至提前结清日利息
     */
    @Column(name = "ET_INTEREST")
    private Double etInterest;

    /**
     * 留购价款
     */
    @Column(name = "NOMINAL_COST")
    private Double nominalCost;

    /**
     * 提前结清手续费
     */
    @Column(name = "ET_FEE")
    private Double etFee;

    /**
     * 其他应付款
     */
    @Column(name = "OTHER_FEE")
    private Double otherFee;

    /**
     * 违约金
     */
    @Column(name = "LIQUIDATED_DAMAGES")
    private Double liquidatedDamages;

    /**
     * 起租日
     */
    @Column(name = "LEASE_START_DATE")
    private Date leaseStartDate;

    /**
     * 减免金额
     */
    @Column(name = "REDUCE_AMOUNT")
    private Double reduceAmount;

    /**
     * 实际应结清金额
     */
    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;

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

    @Column(name = "CALC_FLAG")
    private String calcFlag;

    private Long calcEtInterestDays;
}