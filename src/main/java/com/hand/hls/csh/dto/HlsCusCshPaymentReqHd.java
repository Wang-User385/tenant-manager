package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hls.cont.dto.HlsCusConContractPaymentPt;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.dto.HlsCusPrjProjectInsure;
import com.hand.hls.prj.dto.PrjProjectAttachment;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@Table(name = "csh_payment_req_hd")
@ExtensionAttribute(disable = true)
@Getter
@Setter
public class HlsCusCshPaymentReqHd extends CshPaymentReqHd {
    private Date actualPayDate;
    private String loanType;
    private String paymentType;
    private String paymentApprovedStatus;
    private Long financialAgentId;
    private Long financialUnitId;
    private Long paymentProcessInstanceId;
    private String contractCurrencyId;
    private String sendFlag;
    private String authorityRuleString;
    private Double actualHdPayAmount;

    private Long sourceContractId;

    private Long unitId;

    private Long employeeId;
    @Transient
    private String email;
    @Transient
    private Double dailyRate;
    @Transient
    private Long insureId;
    @Transient
    private String unitIdN;
    @Transient
    private String fundingPlanNumber;
    @Transient
    private String currencyN;
    @Transient
    private String contractCurrency;

    @Transient
    private String companySpvN;
    @Transient
    private Long companySpv;
    @Transient
    private String loanTypeN;
    @Transient
    private String userName;
    @Transient
    private String paymentTypeN;
    @Transient
    private String itemFlag;
    @Transient
    private String paymentApprovedStatusN;
    @Transient
    private String financialAgentIdN;
    @Transient
    private String financialUnitIdN;
    @Transient
    private String accumulatedPaymentAmount;
    @Transient
    private String remainingAmount;
    @Transient
    private List<ProjectCreditCondition> hlsCusConContractPaymentPtList;
    @Transient
    private List<HlsCusPrjProjectInsure> hlsCusPrjProjectInsureList;
    @Transient
    private List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList;
    @Transient
    private List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentList;
    @Transient
    private String planIdN;
    @Transient
    private Double fillAmount;
    @Transient
    private Double dueAmount;
    private Double comCostRate;

    @Transient
    private Long attachmentCount;

    @Transient
    private String paymentReqNumberFrom;
    @Transient
    private String paymentReqNumberTo;

    @Transient
    private Date paymentReqDateFrom;
    @Transient
    private Date paymentReqDateTo;
    @Transient
    private String modifyFlag;
    @Transient
    private String reverseFlag;
    @Transient
    private String bpTenantName;

    @Transient
    private String factoryIdName;


    @Transient
    private String paymentReqStatusN;

    @Transient
    private String paymentMethodN;
    @Transient
    private String paymentBpTypeN;
    @Transient
    private String paymentBpName;
    @Transient
    private Double afterDeductAmount;
    @Transient
    private String projectManagerName;
    /**
     * 业务标示：主要用于二期零售业务，传入值为 RETAIL
     */
    @Transient
    private String businessFlag;




    public List<ProjectCreditCondition> getHlsCusConContractPaymentPtList() {
        return hlsCusConContractPaymentPtList;
    }

    public void setHlsCusConContractPaymentPtList(List<ProjectCreditCondition> hlsCusConContractPaymentPtList) {
        this.hlsCusConContractPaymentPtList = hlsCusConContractPaymentPtList;
    }

    public List<HlsCusPrjProjectInsure> getHlsCusPrjProjectInsureList() {
        return hlsCusPrjProjectInsureList;
    }

    public void setHlsCusPrjProjectInsureList(List<HlsCusPrjProjectInsure> hlsCusPrjProjectInsureList) {
        this.hlsCusPrjProjectInsureList = hlsCusPrjProjectInsureList;
    }

    private Long sourceDocId;
    @Transient
    private Long refProjectId;

    public String getDeductFlag() {
        return deductFlag;
    }

    public void setDeductFlag(String deductFlag) {
        this.deductFlag = deductFlag;
    }

    private String deductFlag;
    @Transient
    private Long bpMasterId;
    @Transient
    private Long projectId;
    @Transient
    private Long quotationId;

    @Transient
    private Long paymentReqId;
    @Transient
    private String paymentReqStatus;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    private String paymentMethod;
    private Double payAmount;

    private String deductWriteOffFlag;

    public Long getPaymentLnId() {
        return paymentLnId;
    }

    public void setPaymentLnId(Long paymentLnId) {
        this.paymentLnId = paymentLnId;
    }

    @Transient
    private Long paymentLnId;

    public Long getBpId() {
        return bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    private Long bpId;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    private Long bankAccountId;
    @Transient
    private String paymentNumber;
    private Date proposedLaunchDate;
    @Transient
    private String fundingPlanIdN;
    @Transient
    private String contractNumber;
    @Transient
    private Double loanNetAmount;

    private Double loanTotalAmount;

    @Transient
    private String hostProjectManager;
    @Transient
    private String hostProjectManagerN;
    @Transient
    private String hostUnitName;
    @Transient
    private String assistUnitName;
    @Transient
    private String assistProjectManager;
    @Transient
    private String assistProjectManagerN;
    @Transient
    private Long assistUnitId;
    @Transient
    private Long hostUnitId;
    @Transient
    private String contractName;
    private String projectName;

    private String transferStatus;
    @Transient
    private String transferStatusN;
    @Transient
    private Date creditEffectiveDate;
    @Transient
    private Date creditExpiryDate;
    @Transient
    private Date loanTimeFrom;
    @Transient
    private Date loanTimeTo;
    @Transient
    private Date creditPeriodFrom;
    @Transient
    private Date creditPeriodTo;
    private String conEndTask;
    @Transient
    private Double deductTotalAmount;
    @Transient
    private Double payNoteAmount;
    @Transient
    private Date leaseAccountDate;
    @Transient
    private String finLoanInitialLeaseN;
    @Transient
    private String finLoanInitialLease;
    @Transient
    private String loanInitialLease;
    @Transient
    private String loanInitialLeaseN;
    @Transient
    private Long prjNoticeId;
    @Transient
    private String bpIdN;

    private String trial;
    private String recheck;

    private Long eftProcessInstanceId;


    private Double financeAmount;

    private Double sumToufangAmount;

    private Double contractBalance;
    @Transient
    private String employeeIdN;
    @Transient
    private String unitName;

    @Transient
    private Long planId;

    @Transient
    private String projectManagerIdN;
    @Transient
    private Date proposedLaunchDateFrom;
    @Transient
    private Date proposedLaunchDateTo;

    @Transient
    private String planDateAmount;

    private String paymentStatus;

    @Transient
    private String companyName;
    @Transient
    private Double intRate;
    @Transient
    private Double xirr;
    @Transient
    private Double irr;
    @Transient
    private Long leaseTerm;
    @Transient
    private String financeNote;
    private String other;
    @Transient
    private String assistProjectManageN;
    @Transient
    private String approvalNumber;
    @Transient
    private Double unpaid;
    @Transient
    private String projectNumber;

    private String postItfcFlag;
    private String itfcDocument;
    private String postItfcCode;
    private String postItfcMsg;
    private Date postApprovedDate;
    private Date applyPayDate;
    private Double amount;
    @Transient
    private String bpName;
    @Transient
    private String manufacturerName;
    @Transient
    private String idCardNo;
    @Transient
    private String phone;
    @Transient
    private Long contractId;

    public Long getUnitId() {
        return unitId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getEmployeeIdN() {
        return employeeIdN;
    }

    public void setEmployeeIdN(String employeeIdN) {
        this.employeeIdN = employeeIdN;
    }

    public Double getContractBalance() {
        return contractBalance;
    }

    public void setContractBalance(Double contractBalance) {
        this.contractBalance = contractBalance;
    }

    public Double getSumToufangAmount() {
        return sumToufangAmount;
    }

    public void setSumToufangAmount(Double sumToufangAmount) {
        this.sumToufangAmount = sumToufangAmount;
    }


    public Double getFinanceAmount() {
        return financeAmount;
    }

    public void setFinanceAmount(Double financeAmount) {
        this.financeAmount = financeAmount;
    }

    public Date getLeaseAccountDate() {
        return leaseAccountDate;
    }

    public void setLeaseAccountDate(Date leaseAccountDate) {
        this.leaseAccountDate = leaseAccountDate;
    }

    public String getFinLoanInitialLeaseN() {
        return finLoanInitialLeaseN;
    }

    public void setFinLoanInitialLeaseN(String finLoanInitialLeaseN) {
        this.finLoanInitialLeaseN = finLoanInitialLeaseN;
    }

    public String getFinLoanInitialLease() {
        return finLoanInitialLease;
    }

    public void setFinLoanInitialLease(String finLoanInitialLease) {
        this.finLoanInitialLease = finLoanInitialLease;
    }

    public String getLoanInitialLease() {
        return loanInitialLease;
    }

    public void setLoanInitialLease(String loanInitialLease) {
        this.loanInitialLease = loanInitialLease;
    }

    public String getLoanInitialLeaseN() {
        return loanInitialLeaseN;
    }

    public void setLoanInitialLeaseN(String loanInitialLeaseN) {
        this.loanInitialLeaseN = loanInitialLeaseN;
    }

    public Double getDeductTotalAmount() {
        return deductTotalAmount;
    }

    public void setDeductTotalAmount(Double deductTotalAmount) {
        this.deductTotalAmount = deductTotalAmount;
    }

    public Double getPayNoteAmount() {
        return payNoteAmount;
    }

    public void setPayNoteAmount(Double payNoteAmount) {
        this.payNoteAmount = payNoteAmount;
    }

    public String getConEndTask() {
        return conEndTask;
    }

    public void setConEndTask(String conEndTask) {
        this.conEndTask = conEndTask;
    }

    public Date getLoanTimeFrom() {
        return loanTimeFrom;
    }

    public void setLoanTimeFrom(Date loanTimeFrom) {
        this.loanTimeFrom = loanTimeFrom;
    }

    public Date getLoanTimeTo() {
        return loanTimeTo;
    }

    public void setLoanTimeTo(Date loanTimeTo) {
        this.loanTimeTo = loanTimeTo;
    }

    public Date getCreditPeriodFrom() {
        return creditPeriodFrom;
    }

    public void setCreditPeriodFrom(Date creditPeriodFrom) {
        this.creditPeriodFrom = creditPeriodFrom;
    }

    public Date getCreditPeriodTo() {
        return creditPeriodTo;
    }

    public void setCreditPeriodTo(Date creditPeriodTo) {
        this.creditPeriodTo = creditPeriodTo;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Date getCreditEffectiveDate() {
        return creditEffectiveDate;
    }

    public void setCreditEffectiveDate(Date creditEffectiveDate) {
        this.creditEffectiveDate = creditEffectiveDate;
    }

    public Date getCreditExpiryDate() {
        return creditExpiryDate;
    }

    public void setCreditExpiryDate(Date creditExpiryDate) {
        this.creditExpiryDate = creditExpiryDate;
    }

    public String getTransferStatusN() {
        return transferStatusN;
    }

    public void setTransferStatusN(String transferStatusN) {
        this.transferStatusN = transferStatusN;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }


    public String getHostProjectManager() {
        return hostProjectManager;
    }

    public void setHostProjectManager(String hostProjectManager) {
        this.hostProjectManager = hostProjectManager;
    }

    public String getHostProjectManagerN() {
        return hostProjectManagerN;
    }

    public void setHostProjectManagerN(String hostProjectManagerN) {
        this.hostProjectManagerN = hostProjectManagerN;
    }

    public String getHostUnitName() {
        return hostUnitName;
    }

    public void setHostUnitName(String hostUnitName) {
        this.hostUnitName = hostUnitName;
    }

    public String getAssistUnitName() {
        return assistUnitName;
    }

    public void setAssistUnitName(String assistUnitName) {
        this.assistUnitName = assistUnitName;
    }

    public String getAssistProjectManager() {
        return assistProjectManager;
    }

    public void setAssistProjectManager(String assistProjectManager) {
        this.assistProjectManager = assistProjectManager;
    }

    public String getAssistProjectManagerN() {
        return assistProjectManagerN;
    }

    public void setAssistProjectManagerN(String assistProjectManagerN) {
        this.assistProjectManagerN = assistProjectManagerN;
    }

    public Long getAssistUnitId() {
        return assistUnitId;
    }

    public void setAssistUnitId(Long assistUnitId) {
        this.assistUnitId = assistUnitId;
    }

    public Long getHostUnitId() {
        return hostUnitId;
    }

    public void setHostUnitId(Long hostUnitId) {
        this.hostUnitId = hostUnitId;
    }

    public Double getLoanNetAmount() {
        return loanNetAmount;
    }

    public void setLoanNetAmount(Double loanNetAmount) {
        this.loanNetAmount = loanNetAmount;
    }

    public Double getLoanTotalAmount() {
        return loanTotalAmount;
    }

    public void setLoanTotalAmount(Double loanTotalAmount) {
        this.loanTotalAmount = loanTotalAmount;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public Date getProposedLaunchDate() {
        return proposedLaunchDate;
    }

    public void setProposedLaunchDate(Date proposedLaunchDate) {
        this.proposedLaunchDate = proposedLaunchDate;
    }

    public String getFundingPlanIdN() {
        return fundingPlanIdN;
    }

    public void setFundingPlanIdN(String fundingPlanIdN) {
        this.fundingPlanIdN = fundingPlanIdN;
    }

    public Long getSourceDocLineId() {
        return sourceDocLineId;
    }

    public void setSourceDocLineId(Long sourceDocLineId) {
        this.sourceDocLineId = sourceDocLineId;
    }

    private Long sourceDocLineId;

    public String getSourceDocType() {
        return sourceDocType;
    }

    public void setSourceDocType(String sourceDocType) {
        this.sourceDocType = sourceDocType;
    }

    private String sourceDocType;

    public Long getSourceDocId() {
        return sourceDocId;
    }

    public void setSourceDocId(Long sourceDocId) {
        this.sourceDocId = sourceDocId;
    }

    public Long getBpMasterId() {
        return bpMasterId;
    }

    public void setBpMasterId(Long bpMasterId) {
        this.bpMasterId = bpMasterId;
    }

    public String getDeductWriteOffFlag() {
        return deductWriteOffFlag;
    }

    public void setDeductWriteOffFlag(String deductWriteOffFlag) {
        this.deductWriteOffFlag = deductWriteOffFlag;
    }

    public Double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
    }

    public Long getPaymentReqId() {
        return paymentReqId;
    }

    public void setPaymentReqId(Long paymentReqId) {
        this.paymentReqId = paymentReqId;
    }

    public String getPaymentReqStatus() {
        return paymentReqStatus;
    }

    public void setPaymentReqStatus(String paymentReqStatus) {
        this.paymentReqStatus = paymentReqStatus;
    }

    public Long getPrjNoticeId() {
        return prjNoticeId;
    }

    public void setPrjNoticeId(Long prjNoticeId) {
        this.prjNoticeId = prjNoticeId;
    }


}
