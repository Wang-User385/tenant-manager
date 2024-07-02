package com.hand.hls.fin.dto;

/**
 * add by zhangyu 20180517
 **/

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "ct_lon_contract_change_req")
public class HlsCusCtLonContractChangeReq extends BaseDTO {

    public static final String FIELD_CHANGE_REQ_ID = "changeReqId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_CHANGE_REQ_NUMBER = "changeReqNumber";
    public static final String FIELD_REQ_STATUS = "reqStatus";
    public static final String FIELD_REQ_DATE = "reqDate";
    public static final String FIELD_SUBMIT_DATE = "submitDate";
    public static final String FIELD_SUBMITTED_BY = "submittedBy";
    public static final String FIELD_DESCRIPTION = "description";

    private Long changeReqId; //融资合同变更ID

    private Long contractId; //融资合同ID

    @Length(max = 200)
    private String changeReqNumber; //变更编号

    @Length(max = 30)
    private String reqStatus; //变更状态

    private Date reqDate; //变更日期

    private Date submitDate; //提交日期

    private Long submittedBy; //提交人

    @Length(max = 2000)
    private String description; //说明


    public void setChangeReqId(Long changeReqId) {
        this.changeReqId = changeReqId;
    }

    public Long getChangeReqId() {
        return changeReqId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return contractId;
    }

    public String getChangeReqNumber() {
        return changeReqNumber;
    }

    public void setChangeReqNumber(String changeReqNumber) {
        this.changeReqNumber = changeReqNumber;
    }

    public String getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(String reqStatus) {
        this.reqStatus = reqStatus;
    }

    public Date getReqDate() {
        return reqDate;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public void setSubmittedBy(Long submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Long getSubmittedBy() {
        return submittedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    private String contractNumber;

    @Transient
    private String contractName;

    @Transient
    private Long creditBpId;

    @Transient
    private String creditBpName;

    @Transient
    private Long lonCompanyId;

    @Transient
    private String lonCompanyIdDesc;

    @Transient
    private Double financeAmount;

    @Transient
    private String currency;

    @Transient
    private String currencyName;

    @Transient
    private String documentType;

    @Transient
    private String documentTypeDesc;

    @Transient
    private String contractStatus;

    @Transient
    private String contractStatusDesc;

    @Transient
    private String financingChannel;

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public Long getCreditBpId() {
        return creditBpId;
    }

    public void setCreditBpId(Long creditBpId) {
        this.creditBpId = creditBpId;
    }

    public String getCreditBpName() {
        return creditBpName;
    }

    public void setCreditBpName(String creditBpName) {
        this.creditBpName = creditBpName;
    }

    public Long getLonCompanyId() {
        return lonCompanyId;
    }

    public void setLonCompanyId(Long lonCompanyId) {
        this.lonCompanyId = lonCompanyId;
    }

    public String getLonCompanyIdDesc() {
        return lonCompanyIdDesc;
    }

    public void setLonCompanyIdDesc(String lonCompanyIdDesc) {
        this.lonCompanyIdDesc = lonCompanyIdDesc;
    }

    public Double getFinanceAmount() {
        return financeAmount;
    }

    public void setFinanceAmount(Double financeAmount) {
        this.financeAmount = financeAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTypeDesc() {
        return documentTypeDesc;
    }

    public void setDocumentTypeDesc(String documentTypeDesc) {
        this.documentTypeDesc = documentTypeDesc;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getContractStatusDesc() {
        return contractStatusDesc;
    }

    public void setContractStatusDesc(String contractStatusDesc) {
        this.contractStatusDesc = contractStatusDesc;
    }

    public String getFinancingChannel() {
        return financingChannel;
    }

    public void setFinancingChannel(String financingChannel) {
        this.financingChannel = financingChannel;
    }

    @Transient
    private String newChangeReq;

    @Transient
    private String approvingChangeReq;

    @Transient
    private String approvedChangeReq;

    @Transient
    private String approvedReturnChangeReq;

    @Transient
    private String cancelChangeReq;

    @Transient
    private String queryDateFrom;

    @Transient
    private String queryDateTo;

    public String getNewChangeReq() {
        return newChangeReq;
    }

    public void setNewChangeReq(String newChangeReq) {
        this.newChangeReq = newChangeReq;
    }

    public String getApprovedChangeReq() {
        return approvedChangeReq;
    }

    public void setApprovedChangeReq(String approvedChangeReq) {
        this.approvedChangeReq = approvedChangeReq;
    }

    public String getApprovedReturnChangeReq() {
        return approvedReturnChangeReq;
    }

    public void setApprovedReturnChangeReq(String approvedReturnChangeReq) {
        this.approvedReturnChangeReq = approvedReturnChangeReq;
    }

    public String getApprovingChangeReq() {
        return approvingChangeReq;
    }

    public void setApprovingChangeReq(String approvingChangeReq) {
        this.approvingChangeReq = approvingChangeReq;
    }

    public String getCancelChangeReq() {
        return cancelChangeReq;
    }

    public void setCancelChangeReq(String cancelChangeReq) {
        this.cancelChangeReq = cancelChangeReq;
    }

    public String getQueryDateFrom() {
        return queryDateFrom;
    }

    public void setQueryDateFrom(String queryDateFrom) {
        this.queryDateFrom = queryDateFrom;
    }

    public String getQueryDateTo() {
        return queryDateTo;
    }

    public void setQueryDateTo(String queryDateTo) {
        this.queryDateTo = queryDateTo;
    }

    @Transient
    private Date createTime;

    @Transient
    private String taskDefinitionKey;

    @Transient
    private Long id;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
