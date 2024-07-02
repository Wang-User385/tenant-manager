package com.hand.hls.csh.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/3/5
 * @description:
 */
public class CshDeduction extends BaseDTO {
    @Id
    @GeneratedValue
    private Long writeOffId;
    private String writeOffType;
    private Date writeOffDate;
    private Long cshTransactionId;
    private Double cshWriteOffAmount;
    private Long subsequentCshTrxId;
    private Long subseqCshWriteOffAmount;
    private String reversedFlag;
    private Long reversedWriteOffId;
    private Date reversedDate;
    private Long cashflowId;
    private Long contractId;
    private Long times;
    private Long cfItem;
    private Long cfType;
    private Double writeOffDueAmount;
    private Double writeOffPrincipal;
    private Double writeOffInterest;
    private Long paymentReqLineId;
    private Long paymentReqId;
    @Transient
    private String accountNum;
    @Transient
    private String writeOffFlag;
    @Transient
    private Date fullWriteOffDate;
    @Transient
    private Long transactionId;
    @Transient
    private Date lastReceiveDate;
    @Transient
    private String contractNumber;
    @Transient
    private Double dueAmount;
    @Transient
    private Double interest;
    @Transient
    private String description;
    @Transient
    private Double transactionAmount;
    @Transient
    private Long bpId;
    @Transient
    @JsonFormat(
            pattern = "yyyy-MM-dd"
    )
    private Date dueDate;
    @Transient
    private String bpName;
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
    @Transient
    private Long wId;
    @Transient
    private String rFlag;

    public CshDeduction() {
    }

    public Double getInterest() {
        return this.interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public String getAccountNum() {
        return this.accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public Double getCshWriteOffAmount() {
        return this.cshWriteOffAmount;
    }

    public void setCshWriteOffAmount(Double cshWriteOffAmount) {
        this.cshWriteOffAmount = cshWriteOffAmount;
    }

    public Double getWriteOffDueAmount() {
        return this.writeOffDueAmount;
    }

    public void setWriteOffDueAmount(Double writeOffDueAmount) {
        this.writeOffDueAmount = writeOffDueAmount;
    }

    public Double getWriteOffPrincipal() {
        return this.writeOffPrincipal;
    }

    public void setWriteOffPrincipal(Double writeOffPrincipal) {
        this.writeOffPrincipal = writeOffPrincipal;
    }

    public Double getWriteOffInterest() {
        return this.writeOffInterest;
    }

    public void setWriteOffInterest(Double writeOffInterest) {
        this.writeOffInterest = writeOffInterest;
    }

    public String getrFlag() {
        return this.rFlag;
    }

    public void setrFlag(String rFlag) {
        this.rFlag = rFlag;
    }

    public Long getwId() {
        return this.wId;
    }

    public Double getDueAmount() {
        return this.dueAmount;
    }

    public Double getTransactionAmount() {
        return this.transactionAmount;
    }

    public void setwId(Long wId) {
        this.wId = wId;
    }

    public String getDepositFrom() {
        return this.depositFrom;
    }

    public void setDepositFrom(String depositFrom) {
        this.depositFrom = depositFrom;
    }

    public Date getStartWriteOffDate() {
        return this.startWriteOffDate;
    }

    public void setStartWriteOffDate(Date startWriteOffDate) {
        this.startWriteOffDate = startWriteOffDate;
    }

    public Date getEndWriteOffDate() {
        return this.endWriteOffDate;
    }

    public void setEndWriteOffDate(Date endWriteOffDate) {
        this.endWriteOffDate = endWriteOffDate;
    }

    public Double getStartWriteOffAmount() {
        return this.startWriteOffAmount;
    }

    public void setStartWriteOffAmount(Double startWriteOffAmount) {
        this.startWriteOffAmount = startWriteOffAmount;
    }

    public Double getEndWriteOffAmount() {
        return this.endWriteOffAmount;
    }

    public void setEndWriteOffAmount(Double endWriteOffAmount) {
        this.endWriteOffAmount = endWriteOffAmount;
    }

    public void setDueAmount(Double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getBpName() {
        return this.bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getContractNumber() {
        return this.contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastReceiveDate() {
        return this.lastReceiveDate;
    }

    public void setLastReceiveDate(Date lastReceiveDate) {
        this.lastReceiveDate = lastReceiveDate;
    }

    public String getWriteOffFlag() {
        return this.writeOffFlag;
    }

    public void setWriteOffFlag(String writeOffFlag) {
        this.writeOffFlag = writeOffFlag;
    }

    public Date getFullWriteOffDate() {
        return this.fullWriteOffDate;
    }

    public void setFullWriteOffDate(Date fullWriteOffDate) {
        this.fullWriteOffDate = fullWriteOffDate;
    }

    public Long getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getWriteOffId() {
        return this.writeOffId;
    }

    public void setWriteOffId(Long writeOffId) {
        this.writeOffId = writeOffId;
    }

    public String getWriteOffType() {
        return this.writeOffType;
    }

    public void setWriteOffType(String writeOffType) {
        this.writeOffType = writeOffType;
    }

    public Date getWriteOffDate() {
        return this.writeOffDate;
    }

    public void setWriteOffDate(Date writeOffDate) {
        this.writeOffDate = writeOffDate;
    }

    public Long getCshTransactionId() {
        return this.cshTransactionId;
    }

    public void setCshTransactionId(Long cshTransactionId) {
        this.cshTransactionId = cshTransactionId;
    }

    public Long getSubsequentCshTrxId() {
        return this.subsequentCshTrxId;
    }

    public void setSubsequentCshTrxId(Long subsequentCshTrxId) {
        this.subsequentCshTrxId = subsequentCshTrxId;
    }

    public Long getSubseqCshWriteOffAmount() {
        return this.subseqCshWriteOffAmount;
    }

    public void setSubseqCshWriteOffAmount(Long subseqCshWriteOffAmount) {
        this.subseqCshWriteOffAmount = subseqCshWriteOffAmount;
    }

    public String getReversedFlag() {
        return this.reversedFlag;
    }

    public void setReversedFlag(String reversedFlag) {
        this.reversedFlag = reversedFlag;
    }

    public Long getReversedWriteOffId() {
        return this.reversedWriteOffId;
    }

    public void setReversedWriteOffId(Long reversedWriteOffId) {
        this.reversedWriteOffId = reversedWriteOffId;
    }

    public Date getReversedDate() {
        return this.reversedDate;
    }

    public void setReversedDate(Date reversedDate) {
        this.reversedDate = reversedDate;
    }

    public Long getCashflowId() {
        return this.cashflowId;
    }

    public void setCashflowId(Long cashflowId) {
        this.cashflowId = cashflowId;
    }

    public Long getContractId() {
        return this.contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getTimes() {
        return this.times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public Long getCfItem() {
        return this.cfItem;
    }

    public void setCfItem(Long cfItem) {
        this.cfItem = cfItem;
    }

    public Long getCfType() {
        return this.cfType;
    }

    public void setCfType(Long cfType) {
        this.cfType = cfType;
    }

    public Long getPaymentReqLineId() {
        return this.paymentReqLineId;
    }

    public void setPaymentReqLineId(Long paymentReqLineId) {
        this.paymentReqLineId = paymentReqLineId;
    }

    public Long getPaymentReqId() {
        return this.paymentReqId;
    }

    public void setPaymentReqId(Long paymentReqId) {
        this.paymentReqId = paymentReqId;
    }
}
