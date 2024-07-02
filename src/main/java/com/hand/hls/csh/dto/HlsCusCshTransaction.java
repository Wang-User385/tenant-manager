//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.dto;

import com.hand.hap.core.annotation.Children;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@ExtensionAttribute(disable = true)
@Table(name = "csh_transaction")
@Getter
@Setter
public class HlsCusCshTransaction extends CshTransaction {

    private Date creationDate;
    private String virtualAccountFlag;
    private Double blockAmount;
    private String description;
    @Transient
    private Long  frozenAmount;
    @Transient
    private Long  actualPaymentAmount;
    @Transient
    private Long  paymentAmount;
    @Transient
    private Date  proposedLaunchDate;
    @Transient
    private Date  actualPayDate;
    @Transient
    private String paymentApprovedStatus;
    @Transient
    private String paymentApprovedStatusN;
    @Transient
    private String writeOffStatus;
    @Transient
    private String bankInf;
    @Transient
    private String bpName;
    @Transient
    private String matchStatus;
    @Transient
    private Date transactionDateFrom;
    @Transient
    private Date transactionDateTo;
    @Transient
    private Date queryDateTo;
    @Transient
    private Double unWriteOffAmount;

    private String importFlag;

    private Date importDate;

    private String reversedDescription;
    @Transient
    private Double unWriteOffAmountFrom;
    @Transient
    private Double unWriteOffAmountTo;

    @Transient
    private Double refundAmount;

    @Transient
    private Double advanceReceiptAmount;

    @Transient
    @Children
    List<HlsCusCshWriteOff> cshWriteOffList;

    @Transient
    @Children
    List<HlsCusWriteOffMatch> creditCshWriteOffMatchs;

    @Transient
    @Children
    List<HlsCusWriteOffMatch> advanceCshWriteOffMacths;

    @Transient
    @Children
    List<HlsCusWriteOffMatch> depositCshWriteOffMatchs;

    /**
     * 核销为预收款list（二期新增）
     */
    @Transient
    @Children
    List<CshAllocationAdvance> cshAllocationAdvanceList;

    /**
     * 核销为保证金list（二期新增）
     */
    @Transient
    @Children
    List<CshAllocationDeposit> cshAllocationDepositList;

    @Transient
    private Long roleId;
    @Transient
    private String roleCode;

    @Transient
    private Double financeAmount;

    @Transient
    private Double preOccupyAmount;

    @Transient
    private String guarantorBpName;

    @Transient
    private String contractStatus;

    @Transient
    private String cfItemDesc;

    @Transient
    private String refundStatusDesc;

    @Transient
    private String refundStatus;

    @Transient
    private String transactionIdStr;
    @Transient
    private List<Long> transactionIdS;

    @Transient
    private String businessTypeN;

    @Transient
    private String currencyCodeN;

    @Transient
    private Double writeOffAmountDetail;

    @Transient
    private String  accountingPeriod;

    @Transient
    private String notTransactionIdStr;

    @Transient
    private List<Long> notTransactionIdS;

    @Transient
    private String isReFund;

    @Transient
    private String periodNumber;

    @Transient
    private String refundNumber;

    @Transient
    private String allocationIdStr;

    @Transient
    private String isAllocationFlag;

    @Transient
    private String canRefund;

    @Transient
    private Long refundId;

    @Transient
    private Long paymentReqId;

    @Transient
    private Long fundingPlanId;

    @Transient
    private String bpIdN;

    @Transient
    private String contractName;

    @Transient
    private String paymentNumber;
    @Transient
    private String hostProjectManager;
    @Transient
    private String hostUnitName;
    @Transient
    private String trial;
    @Transient
    private String recheck;


    @Transient
    private Double allocationAmount;
    @Transient
    private String allocationSource;
    @Transient
    private Long allocationId;
    @Transient
    private Double advanceWriteOffAmount;
    @Transient
    private Long advanceCshTrxId;
    @Transient
    private Double unWriteOffDueAmount;

    /**
     * 二期功能： 现金事务类型字符串数组
     */
    @Transient
    private String[] transactionTypes;

    @Transient
    private Long paymentBpId;

    private String flowNo;

    private String pkEbankDzd;
    private String postItfcFlag;
    private String postItfcCode;
    private String postItfcMsg;
    private Date postApprovedDate;
    private String itfcArrNumber;


    @Transient
    private String tenantId;
    @Transient
    private String tenantIdN;
    @Transient
    private String autoDeductFlag;
    @Transient
    private String autoDeductFlagN;

    /**
     * 保证金抵扣方式
     */
    @Transient
    private String depositDeductMethod;
    @Transient
    private String depositDeductMethodN;

    @Transient
    private Long manageUserId;
    @Transient
    private Long manufacturerId;
    @Transient
    private String manufacturerIdN;
    @Transient
    private String factoryIdN;
    @Transient
    private String bpIdVenderN;
    @Transient
    private Date orderStartDate;
    @Transient
    private Date orderEndDate;
    @Transient
    private String inDeductFlag;
    @Transient
    private String modifyFlag;
    @Transient
    private Double canDeductAmount;
    /**
     * 拟抵扣后金额
     */
    @Transient
    private Double afterDeductAmount;
    @Transient
    private Long employeeId;

    @Transient
    private String employeeIdN;
    @Transient
    private String leaseChannel;
    /**
     * 保证金总额
     */
    @Transient
    private Double transactionAmountSum;

    /**
     * 拟抵扣金额
     */
    @Transient
    private Double canDeductAmountSum;

    /**
     * 拟抵扣后金额
     */
    @Transient
    private Double canReturnAmountSum;
    @Transient
    private Date rentDueDate;

    @Transient
    private Double creditPrincipal;
    @Transient
    private Double creditInterest;

    public HlsCusCshTransaction() {
    }

    public HlsCusCshTransaction(CshTransaction cshTransaction) {
        super(cshTransaction);
    }
}
