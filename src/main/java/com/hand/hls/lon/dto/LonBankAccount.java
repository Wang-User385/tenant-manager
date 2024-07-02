package com.hand.hls.lon.dto;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(disable = true)
@Table(name = "ct_lon_bank_account")
public class LonBankAccount extends BaseDTO {

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
    private String bankDepositName;
    private String bankAccountType ;
    @Transient
    private String bankAccountTypeN;
    @Transient
    private String companyIdN;

    public String getCompanyIdN() {
        return companyIdN;
    }

    public void setCompanyIdN(String companyIdN) {
        this.companyIdN = companyIdN;
    }

    public String getBankAccountTypeN() {
        return bankAccountTypeN;
    }

    public void setBankAccountTypeN(String bankAccountTypeN) {
        this.bankAccountTypeN = bankAccountTypeN;
    }

    public String getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String getBankDepositName() {
        return bankDepositName;
    }

    public void setBankDepositName(String bankDepositName) {
        this.bankDepositName = bankDepositName;
    }

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
}