package com.hand.hls.csh.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Created by lpc on 2017/9/30.
 */
public class CshPaymentReqHd extends BaseDTO {
    @Id
    @GeneratedValue
    private Long paymentReqId;
    private Long companyId;
    private String documentType;
    private String documentCategory;
    private String businessType;
    private String paymentReqNumber;
    private Date paymentReqDate;
    private String paymentReqStatus;
    private Date applyPayDate;
    private Double amount;
    private String currency;
    private String description;
    private Long processInstanceId;
    private Long fundingPlanId;

    private String bpName;
    @Transient
    private String user_name;

    public Long getFundingPlanId() {
        return fundingPlanId;
    }

    public void setFundingPlanId(Long fundingPlanId) {
        this.fundingPlanId = fundingPlanId;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    @Transient
    private String currencyName;
    @Transient
    private List<HlsCusCshPaymentReqLn> reqLns;

    @Transient
    private String paymentFlagDesc;

    @Transient
    private String paymentFlag;


    @Transient
    private String paymentAmount1;

    @Transient
    private String paymentAmount2;

    @Transient
    private String paymentAmount3;

    @Transient
    private Double amountFrom;

    @Transient
    private Double amountTo;

    @Transient
    private String paymentReqStatusDesc;

    @Transient
    private String listReq;

    @Transient
    private String listFlag;
    @Transient
    private String bankAccountNum;
    @Transient
    private String bankBranchName;


    @Transient
    private String descriptionLn;
    @Transient
    private String applyAmountLn;
    @Transient
    private Long paymentReqLnId;

    @Transient
    private Long withdrawCfId;
    @Transient
    private Long withdrawId;
    @Transient
    private String withdrawNumber;
    @Transient
    private Long contractId;

    private String bpBankName;

    private String bpBankBranchName;

    private String bpBankAccountNum;

    private String bpBankAccountName;

    private Long bpBankAccountId;

    @Transient
    private String queryInfo;

    public String getQueryInfo() {
        return queryInfo;
    }

    public void setQueryInfo(String queryInfo) {
        this.queryInfo = queryInfo;
    }


    public Long getBpBankAccountId() {
        return bpBankAccountId;
    }

    public void setBpBankAccountId(Long bpBankAccountId) {
        this.bpBankAccountId = bpBankAccountId;
    }

    public Long getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(Long withdrawId) {
        this.withdrawId = withdrawId;
    }

    public Long getWithdrawCfId() {
        return withdrawCfId;
    }

    public void setWithdrawCfId(Long withdrawCfId) {
        this.withdrawCfId = withdrawCfId;
    }

    public Long getPaymentReqLnId() {
        return paymentReqLnId;
    }

    public void setPaymentReqLnId(Long paymentReqLnId) {
        this.paymentReqLnId = paymentReqLnId;
    }

    public String getBankAccountNum() {
        return bankAccountNum;
    }

    public void setBankAccountNum(String bankAccountNum) {
        this.bankAccountNum = bankAccountNum;
    }

    public String getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getDescriptionLn() {
        return descriptionLn;
    }

    public void setDescriptionLn(String descriptionLn) {
        this.descriptionLn = descriptionLn;
    }

    public String getApplyAmountLn() {
        return applyAmountLn;
    }

    public void setApplyAmountLn(String applyAmountLn) {
        this.applyAmountLn = applyAmountLn;
    }


    /**
     * @return the reqLns
     */
    public List<HlsCusCshPaymentReqLn> getReqLns() {
        return reqLns;
    }

    /**
     * @param reqLns the reqLns to set
     */
    public void setReqLns(List<HlsCusCshPaymentReqLn> reqLns) {
        this.reqLns = reqLns;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HlsCusCshPaymentReqHd [payment_req_id=" + paymentReqId + ", company_id=" + companyId + ", document_type="
                + documentType + ", document_category=" + documentCategory + ", business_type=" + businessType
                + ", payment_req_number=" + paymentReqNumber + ", payment_req_date=" + paymentReqDate
                + ", payment_req_status=" + paymentReqStatus + ", apply_pay_date=" + applyPayDate + ", amount="
                + amount + ", currency=" + currency + ", description=" + description + ", reqLns=" + reqLns + "]";
    }

    public String getWithdrawNumber() {
        return withdrawNumber;
    }

    public void setWithdrawNumber(String withdrawNumber) {
        this.withdrawNumber = withdrawNumber;
    }

    public String getBpBankName() {
        return bpBankName;
    }

    public void setBpBankName(String bpBankName) {
        this.bpBankName = bpBankName;
    }

    public String getBpBankBranchName() {
        return bpBankBranchName;
    }

    public void setBpBankBranchName(String bpBankBranchName) {
        this.bpBankBranchName = bpBankBranchName;
    }

    public String getBpBankAccountNum() {
        return bpBankAccountNum;
    }

    public void setBpBankAccountNum(String bpBankAccountNum) {
        this.bpBankAccountNum = bpBankAccountNum;
    }

    public String getBpBankAccountName() {
        return bpBankAccountName;
    }

    public void setBpBankAccountName(String bpBankAccountName) {
        this.bpBankAccountName = bpBankAccountName;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }




    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentCategory() {
        return documentCategory;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getPaymentReqNumber() {
        return paymentReqNumber;
    }

    public void setPaymentReqNumber(String paymentReqNumber) {
        this.paymentReqNumber = paymentReqNumber;
    }

    public Date getPaymentReqDate() {
        return paymentReqDate;
    }

    public void setPaymentReqDate(Date paymentReqDate) {
        this.paymentReqDate = paymentReqDate;
    }


    public Date getApplyPayDate() {
        return applyPayDate;
    }

    public void setApplyPayDate(Date applyPayDate) {
        this.applyPayDate = applyPayDate;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getPaymentFlagDesc() {
        return paymentFlagDesc;
    }

    public void setPaymentFlagDesc(String paymentFlagDesc) {
        this.paymentFlagDesc = paymentFlagDesc;
    }

    public String getPaymentFlag() {
        return paymentFlag;
    }

    public void setPaymentFlag(String paymentFlag) {
        this.paymentFlag = paymentFlag;
    }

    public String getPaymentAmount1() {
        return paymentAmount1;
    }

    public void setPaymentAmount1(String paymentAmount1) {
        this.paymentAmount1 = paymentAmount1;
    }

    public String getPaymentAmount2() {
        return paymentAmount2;
    }

    public void setPaymentAmount2(String paymentAmount2) {
        this.paymentAmount2 = paymentAmount2;
    }

    public String getPaymentAmount3() {
        return paymentAmount3;
    }

    public void setPaymentAmount3(String paymentAmount3) {
        this.paymentAmount3 = paymentAmount3;
    }

    public Double getAmountFrom() {
        return amountFrom;
    }

    public void setAmountFrom(Double amountFrom) {
        this.amountFrom = amountFrom;
    }

    public Double getAmountTo() {
        return amountTo;
    }

    public void setAmountTo(Double amountTo) {
        this.amountTo = amountTo;
    }

    public String getPaymentReqStatusDesc() {
        return paymentReqStatusDesc;
    }

    public void setPaymentReqStatusDesc(String paymentReqStatusDesc) {
        this.paymentReqStatusDesc = paymentReqStatusDesc;
    }

    public String getListReq() {
        return listReq;
    }

    public void setListReq(String listReq) {
        this.listReq = listReq;
    }

    public String getListFlag() {
        return listFlag;
    }

    public void setListFlag(String listFlag) {
        this.listFlag = listFlag;
    }
}
