package com.hand.hls.csh.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

@Getter
@Setter
public class CshWriteOff extends BaseDTO {
    @Id
    @GeneratedValue
    private Long writeOffId;
    private String writeOffType;
    private Date writeOffDate;
    private Long cshTransactionId;
    private Double cshWriteOffAmount;
    private Long subsequentCshTrxId;
    private Double subseqCshWriteOffAmount;
    private String reversedFlag;
    private Long reversedWriteOffId;
    private Date reversedDate;
    private Long cashflowId;
    @Condition(
            operator = "LIKE"
    )
    private Long contractId;
    private Long times;
    private Long cfItem;
    private Long cfType;
    private String description;
    private String writeOffDocCategory;
    private Long withdrawId;
    private Long repaymentId;
    private Long withdrawCfId;
    private Date lastUpdateDate;
    @Transient
    private String bankSlipNum;
    @Transient
    private Double withdrawAmount;
    @Transient
    private String withdrawNumber;
    private Double writeOffDueAmount;
    @Transient
    private String writeOffPrincipalFormat;
    @Transient
    private String writeOffInterestFormat;
    @Transient
    private String writeOffDueAmountFormat;
    @Transient
    private Date dayEndDate;
    @Transient
    private Date penaltyCalcDate;
    @Transient
    private String currencyCode;
    private Double writeOffPrincipal;
    private Double writeOffInterest;
    private Long paymentReqLineId;
    private Long paymentReqId;
    @Transient
    private Date writeOffDateFrom;
    @Transient
    private Date writeOffDateTo;
    @Transient
    private Double cshWriteOffAmountFrom;
    @Transient
    private Double cshWriteOffAmountTo;
    @Transient
    private String bpName;
    @Transient
    private String transactionNum;
    @Transient
    private Date transactionDate;
    @Transient
    @Condition(
            operator = "LIKE"
    )
    private String contractNumber;
    @Transient
    private Double transactionAmount;
    @Transient
    private String bankAccountName;
    @Transient
    private String bankAccountNum;
    @Transient
    private Long bankAccountId;
    @Transient
    @Condition(
            operator = "LIKE"
    )
    private String contractName;
    @Transient
    private String cfItemDesc;
    @Transient
    private Double surplusAmount;
    @Transient
    private String cfTypeDesc;
    @Transient
    private String bankBranchName;
    @Transient
    private String companyShortName;
    @Transient
    private String bpBankAccountName;
    @Transient
    private String accountNum;
    @Transient
    private Long companyId;
    @Transient
    private String writeOffFlag;
    @Transient
    private Date fullWriteOffDate;
    @Transient
    private Long transactionId;
    @Transient
    private Date lastReceiveDate;
    @Transient
    private Double dueAmount;
    @Transient
    private Long bpId;
    @Transient
    @JsonFormat(
            pattern = "yyyy-MM-dd"
    )
    private Date dueDate;
    @Transient
    private String depositFrom;
    @Transient
    private Date startWriteOffDate;
    @Transient
    private Date endWriteOffDate;
    @Transient
    private Double startWriteOffAmount;
    @Transient
    private Double endWriteOffAmount;
    /*    @Transient
        private Long wId;
        @Transient
        private String rFlag;*/
    @Transient
    private String division;
    @Transient
    private String documentType;
    @Transient
    private Double conlNUM;
    @Transient
    private Double conlbNUM;
    @Transient
    private Long writeOffMonth;
    @Transient
    private String bpBankAccountNum;
    @Transient
    private HlsCusCshTransaction cshTransaction;
    @Transient
    private String remarks;
    @Transient
    private String paymentMethod;
    @Transient
    private String strDate;
    @Transient
    public static final String RECEIPT_CREDIT = "RECEIPT_CREDIT";
    @Transient
    public static final String ADVANCE_RECEIPT_CREDIT = "ADVANCE_RECEIPT_CREDIT";
    @Transient
    public static final String RECEIPT_ADVANCE_RECEIPT = "RECEIPT_ADVANCE_RECEIPT";
    @Transient
    public static final String RECEIPT_DEPOSIT = "RECEIPT_DEPOSIT";
    @Transient
    public static final String RECEIPT_DEPOSIT_POOL = "RECEIPT_DEPOSIT_POOL";
    @Transient
    public static final String PAYMENT_DEBT = "PAYMENT_DEBT";
    @Transient
    public static final String REFUND = "REFUND";
    @Transient
    private String billing_status;
    @Transient
    private Long queryTimeSolt;
    @Transient
    private Long timeCount;
    @Transient
    private Date LAST_UPDATE_DATE;
    @Transient
    private String userName;
    @Transient
    private String billingStatus;
    @Transient
    private String cf_direction;
    @Transient
    private String queryCondition;

    public CshWriteOff() {
    }

    public CshWriteOff(CshWriteOff cshWriteOff) {
        this.writeOffId = cshWriteOff.writeOffId;
        this.writeOffType = cshWriteOff.writeOffType;
        this.writeOffDate = cshWriteOff.writeOffDate;
        this.cshTransactionId = cshWriteOff.cshTransactionId;
        this.cshWriteOffAmount = cshWriteOff.cshWriteOffAmount;
        this.subsequentCshTrxId = cshWriteOff.subsequentCshTrxId;
        this.subseqCshWriteOffAmount = cshWriteOff.subseqCshWriteOffAmount;
        this.reversedFlag = cshWriteOff.reversedFlag;
        this.reversedWriteOffId = cshWriteOff.reversedWriteOffId;
        this.reversedDate = cshWriteOff.reversedDate;
        this.cashflowId = cshWriteOff.cashflowId;
        this.contractId = cshWriteOff.contractId;
        this.times = cshWriteOff.times;
        this.cfItem = cshWriteOff.cfItem;
        this.cfType = cshWriteOff.cfType;
        this.description = cshWriteOff.description;
        this.writeOffDueAmount = cshWriteOff.writeOffDueAmount;
        this.writeOffPrincipal = cshWriteOff.writeOffPrincipal;
        this.writeOffInterest = cshWriteOff.writeOffInterest;
        this.paymentReqLineId = cshWriteOff.paymentReqLineId;
        this.paymentReqId = cshWriteOff.paymentReqId;
        this.writeOffDateFrom = cshWriteOff.writeOffDateFrom;
        this.writeOffDateTo = cshWriteOff.writeOffDateTo;
        this.cshWriteOffAmountFrom = cshWriteOff.cshWriteOffAmountFrom;
        this.cshWriteOffAmountTo = cshWriteOff.cshWriteOffAmountTo;
        this.bpName = cshWriteOff.bpName;
        this.transactionNum = cshWriteOff.transactionNum;
        this.contractNumber = cshWriteOff.contractNumber;
        this.transactionAmount = cshWriteOff.transactionAmount;
        this.bankAccountName = cshWriteOff.bankAccountName;
        this.bankAccountNum = cshWriteOff.bankAccountNum;
        this.contractName = cshWriteOff.contractName;
        this.cfItemDesc = cshWriteOff.cfItemDesc;
        this.cfTypeDesc = cshWriteOff.cfTypeDesc;
        this.cshTransaction = cshWriteOff.cshTransaction;
        this.remarks = cshWriteOff.remarks;
        this.surplusAmount = cshWriteOff.surplusAmount;
    }

    /*public String toString() {
        return "CshWriteOff [writeOffId=" + this.writeOffId + ", writeOffType=" + this.writeOffType + ", writeOffDate=" + this.writeOffDate + ", cshTransactionId=" + this.cshTransactionId + ", cshWriteOffAmount=" + this.cshWriteOffAmount + ", subsequentCshTrxId=" + this.subsequentCshTrxId + ", subseqCshWriteOffAmount=" + this.subseqCshWriteOffAmount + ", reversedFlag=" + this.reversedFlag + ", reversedWriteOffId=" + this.reversedWriteOffId + ", reversedDate=" + this.reversedDate + ", cashflowId=" + this.cashflowId + ", contractId=" + this.contractId + ", times=" + this.times + ", cfItem=" + this.cfItem + ", cfType=" + this.cfType + ", description=" + this.description + ", writeOffDueAmount=" + this.writeOffDueAmount + ", writeOffPrincipal=" + this.writeOffPrincipal + ", writeOffInterest=" + this.writeOffInterest + ", paymentReqLineId=" + this.paymentReqLineId + ", paymentReqId=" + this.paymentReqId + ", writeOffDateFrom=" + this.writeOffDateFrom + ", writeOffDateTo=" + this.writeOffDateTo + ", cshWriteOffAmountFrom=" + this.cshWriteOffAmountFrom + ", cshWriteOffAmountTo=" + this.cshWriteOffAmountTo + ", bpName=" + this.bpName + ", transactionNum=" + this.transactionNum + ", contractNumber=" + this.contractNumber + ", transactionAmount=" + this.transactionAmount + ", bankAccountName=" + this.bankAccountName + ", bankAccountNum=" + this.bankAccountNum + ", contractName=" + this.contractName + ", cfItemDesc=" + this.cfItemDesc + ", surplusAmount=" + this.surplusAmount + ", cfTypeDesc=" + this.cfTypeDesc + ", bankBranchName=" + this.bankBranchName + ", companyShortName=" + this.companyShortName + ", bpBankAccountName=" + this.bpBankAccountName + ", accountNum=" + this.accountNum + ", writeOffFlag=" + this.writeOffFlag + ", fullWriteOffDate=" + this.fullWriteOffDate + ", transactionId=" + this.transactionId + ", lastReceiveDate=" + this.lastReceiveDate + ", dueAmount=" + this.dueAmount + ", bpId=" + this.bpId + ", dueDate=" + this.dueDate + ", depositFrom=" + this.depositFrom + ", startWriteOffDate=" + this.startWriteOffDate + ", endWriteOffDate=" + this.endWriteOffDate + ", startWriteOffAmount=" + this.startWriteOffAmount + ", endWriteOffAmount=" + this.endWriteOffAmount + ", wId=" + this.wId + ", rFlag=" + this.rFlag + ", division=" + this.division + ", documentType=" + this.documentType + ", conlNUM=" + this.conlNUM + ", conlbNUM=" + this.conlbNUM + ", writeOffMonth=" + this.writeOffMonth + ", bpBankAccountNum=" + this.bpBankAccountNum + ", cshTransaction=" + this.cshTransaction + ", remarks=" + this.remarks + ", paymentMethod=" + this.paymentMethod + ", billing_status=" + this.billing_status + ", queryTimeSolt=" + this.queryTimeSolt + ", timeCount=" + this.timeCount + ", queryCondition=" + this.queryCondition + "]";
    }*/
}

