//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "csh_write_off"
)
@Getter
@Setter
public class HlsCusCshWriteOff extends CshWriteOff {

    @Transient
    private Long writeOffMatchId;

    private String arriveModality;

    private Long advanceBpId;

    /**
     * 是否插入承租人还款信息表
     */
    @Transient
    private String insertFlag;

    @Transient
    private Long depositDeductionId;

    @Transient
    private String approvalNumber;
    @Transient
    private String paymentNumber;

    private String importFlag;

    @Transient
    private List<HlsCusCshWriteOff> hlsCusCshWriteOffList;

    public HlsCusCshWriteOff() {
    }

    //收款退款需求字段
    @Transient
    private Long companyId;//公司ID

    @Transient
    private Long unitId;//部门ID

    @Transient
    private String refundFlag;

    @Transient
    private String nonRefundFlag;

    @Transient
    private String writeOffTypeN;

    @Transient
    private Date dueDate;

    @Transient
    private String cfItemN;

    @Transient
    private String tenantName;

    @Transient
    private String reversedFlagN;

    @Transient
    private String transactionIdStr;

    @Transient
    private String advanceReceiptAmountStr;

    @Transient
    private Double unWriteOffAmount;

    @Transient
    private Long writeOffOrder;

    @Transient
    private Double allocationAmount;

    @Transient
    private String allocationSource;
    @Transient
    private Long allocationId;

    @Transient
    private String transactionType;

    private String firstLeasePayFlag;


    @Transient
    private String projectNumber;
    @Transient
    private String companySpvN;
    @Transient
    private String bpCode;
    @Transient
    private String hostProjectManagerN;
    @Transient
    private String hostUnitName;

    @Transient
    private Double dueAmount;
    @Transient
    private Double netDueAmount;
    @Transient
    private Double vatDueAmount;
    @Transient
    private Double principal;
    @Transient
    private Double netPrincipal;
    @Transient
    private Double vatPrincipal;
    @Transient
    private Double interest;
    @Transient
    private Double vatInterest;
    @Transient
    private Double netInterest;
    @Transient
    private Double writeOffDueAmountFrom;
    @Transient
    private Double writeOffDueAmountTo;
    @Transient
    private String reversedReceiptFlag;
    @Transient
    private String allocationIdStr;
    @Transient
    private Date calcDate;
    @Transient
    private String prjContractNumber;

    private Date dealDate;

    @Transient
    private String flowNo;

    private Long deductReqLnId;

    @Transient
    private String flowStatus;
}
