package com.hand.hls.fin.dto;

/**
 * created by zhangyu on 2018/6/22
 **/

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "ct_lon_bank_account")
public class HlsCusCtLonBankAccount extends BaseDTO {

    public static final String FIELD_BANK_ACCOUNT_ID = "bankAccountId";
    public static final String FIELD_BANK_NAME = "bankName";
    public static final String FIELD_BANK_BRANCH_NAME = "bankBranchName";
    public static final String FIELD_BANK_ACCOUNT_NAME = "bankAccountName";
    public static final String FIELD_BANK_ACCOUNT_NUM = "bankAccountNum";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_BP_ID = "bpId";
    public static final String FIELD_COMPANY_ID = "companyId";

    @Id
    @GeneratedValue
    private Long bankAccountId; //银行账户ID

    @Length(max = 200)
    @Condition(operator = LIKE)
    private String bankName; //银行名称

    @Length(max = 200)
    @Condition(operator = LIKE)
    private String bankBranchName; //支行名称

    @Length(max = 200)
    @Condition(operator = LIKE)
    private String bankAccountName; //账户

    @Length(max = 200)
    @Condition(operator = LIKE)
    private String bankAccountNum; //账号

    @Length(max = 10)
    private String enabledFlag; //启用标志

    private Long bpId; //授信机构ID

    private Long companyId; //公司ID

    /**
     * 备注
     */
    private String description;


    //选择的收付管理的银行账户
    private Long cshBankAccountId;

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public Long getBpId() {
        return bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountNum() {
        return bankAccountNum;
    }

    public void setBankAccountNum(String bankAccountNum) {
        this.bankAccountNum = bankAccountNum;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCshBankAccountId() {
        return cshBankAccountId;
    }

    public void setCshBankAccountId(Long cshBankAccountId) {
        this.cshBankAccountId = cshBankAccountId;
    }

    @Transient
    private Long changeReqId;

    @Transient
    private String changeReqNumber; //变更编号

    @Transient
    private String reqStatus; //变更状态

    @Transient
    private Date reqDate; //变更日期

    @Transient
    private Date submitDate; //提交日期

    @Transient
    private Long submittedBy; //提交人

    @Transient
    private Long contractId;

    @Transient
    private Long withdrawId;

    public Long getChangeReqId() {
        return changeReqId;
    }

    public void setChangeReqId(Long changeReqId) {
        this.changeReqId = changeReqId;
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

    public Long getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(Long submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(Long withdrawId) {
        this.withdrawId = withdrawId;
    }
}
