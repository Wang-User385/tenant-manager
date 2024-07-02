package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import groovy.transform.TailRecursive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Getter
@Setter

public class HlsCusCshPaymentTran extends BaseDTO {
    private List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLns;

    private List<HlsCusCshTransactionDtl> hlsCusCshTransactionDtls;

    @Transient
    private Double transactionAmount;

    @Transient
    private Date transactionDate;

    @Transient
    private String currency;

    @Transient
    private Long contractId;

    @Transient
    private String itemFlag;

    @Transient
    private Long fundingPlanId;

    @Transient
    private Long paymentReqId;

    /**
     * 业务标示：主要用于二期零售业务，传入值为 RETAIL
     */
    @Transient
    private String businessFlag;

    /**
     * 二期功能：退款申请支付-退款申请头ID
     */
    @Transient
    private Long refundId;


    public String getBusinessFlag() {
        return businessFlag;
    }

    public void setBusinessFlag(String businessFlag) {
        this.businessFlag = businessFlag;
    }

    public Long getPaymentReqId() {
        return paymentReqId;
    }

    public void setPaymentReqId(Long paymentReqId) {
        this.paymentReqId = paymentReqId;
    }

    public List<HlsCusCshPaymentReqLn> getHlsCusCshPaymentReqLns() {
        return hlsCusCshPaymentReqLns;
    }

    public void setHlsCusCshPaymentReqLns(List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLns) {
        this.hlsCusCshPaymentReqLns = hlsCusCshPaymentReqLns;
    }

    public List<HlsCusCshTransactionDtl> getHlsCusCshTransactionDtls() {
        return hlsCusCshTransactionDtls;
    }

    public void setHlsCusCshTransactionDtls(List<HlsCusCshTransactionDtl> hlsCusCshTransactionDtls) {
        this.hlsCusCshTransactionDtls = hlsCusCshTransactionDtls;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getItemFlag() {
        return itemFlag;
    }

    public void setItemFlag(String itemFlag) {
        this.itemFlag = itemFlag;
    }

    public Long getFundingPlanId() {
        return fundingPlanId;
    }

    public void setFundingPlanId(Long fundingPlanId) {
        this.fundingPlanId = fundingPlanId;
    }
}
