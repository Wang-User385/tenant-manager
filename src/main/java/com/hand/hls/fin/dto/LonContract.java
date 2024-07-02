//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.fin.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.validator.constraints.Length;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "lon_contract"
)
public class LonContract extends BaseDTO {
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_DOCUMENT_TYPE = "documentType";
    public static final String FIELD_DOCUMENT_CATEGORY = "documentCategory";
    public static final String FIELD_BUSINESS_TYPE = "businessType";
    public static final String FIELD_CONTRACT_NUMBER = "contractNumber";
    public static final String FIELD_CONTRACT_NAME = "contractName";
    public static final String FIELD_CONTRACT_STATUS = "contractStatus";
    public static final String FIELD_CREDIT_CONTRACT_ID = "creditContractId";
    public static final String FIELD_CREDIT_LINE_ID = "creditLineId";
    public static final String FIELD_CURRENCY = "currency";
    public static final String FIELD_CREDIT_BP_ID = "creditBpId";
    public static final String FIELD_ORIGINAL_CONTRACT_NUMBER = "originalContractNumber";
    public static final String FIELD_FINANCE_AMOUNT = "financeAmount";
    public static final String FIELD_WITHDRAW_BANK_ACCOUNT_ID = "withdrawBankAccountId";
    public static final String FIELD_REPAYMENT_BANK_ACCOUNT_ID = "repaymentBankAccountId";
    public static final String FIELD_DESCRIPTION = "description";
    @Id
    @GeneratedValue
    private Long contractId;
    private Long companyId;
    @Length(
            max = 100
    )
    private String documentType;
    @Length(
            max = 100
    )
    private String documentCategory;
    @Length(
            max = 100
    )
    private String businessType;
    @Length(
            max = 255
    )
    private String contractNumber;
    @Length(
            max = 500
    )
    private String contractName;
    @Length(
            max = 100
    )
    private String contractStatus;
    private Long creditContractId;
    private Long creditLineId;
    @Length(
            max = 100
    )
    private String currency;
    private Long creditBpId;
    @Length(
            max = 255
    )
    private String originalContractNumber;
    private Double financeAmount;
    private Long withdrawBankAccountId;
    private Long repaymentBankAccountId;
    @Length(
            max = 500
    )
    private String description;
    @Transient
    private String creditBpName;
    @Transient
    private String businessDesc;
    @Transient
    private String contractStatusDesc;
    @Transient
    private String creditContractNumber;
    @Transient
    private String creditContractName;
    @Transient
    private String creditLineDesc;
    @Transient
    private String withdrawBankAccountName;
    @Transient
    private String withdrawBankAccountNumber;
    @Transient
    private String repaymentBankAccountName;
    @Transient
    private String repaymentBankAccountNumber;
    @Transient
    private String withdrawBankName;
    @Transient
    private String withdrawBankAccountBranchName;
    @Transient
    private Double withdrawApprovedAmount;
    @Transient
    private Double withdrawLeftAmount;
    @Transient
    private Double loanRate;
    @Transient
    private String documentTypeDesc;
    @Transient
    private String currencyName;
    @Transient
    private String documentTypes;
    @Transient
    private String contractStatuses;
    @Transient
    private String amtFirst;
    @Transient
    private String amtSecond;
    @Transient
    private String amtThird;
    @Transient
    private Double amtFrom;
    @Transient
    private Double amtTo;
    @Transient
    private String manager;

    public LonContract() {
    }

    public String getManager() {
        return this.manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getDocumentTypes() {
        return this.documentTypes;
    }

    public void setDocumentTypes(String documentTypes) {
        this.documentTypes = documentTypes;
    }

    public String getContractStatuses() {
        return this.contractStatuses;
    }

    public void setContractStatuses(String contractStatuses) {
        this.contractStatuses = contractStatuses;
    }

    public String getAmtFirst() {
        return this.amtFirst;
    }

    public void setAmtFirst(String amtFirst) {
        this.amtFirst = amtFirst;
    }

    public String getAmtSecond() {
        return this.amtSecond;
    }

    public void setAmtSecond(String amtSecond) {
        this.amtSecond = amtSecond;
    }

    public String getAmtThird() {
        return this.amtThird;
    }

    public void setAmtThird(String amtThird) {
        this.amtThird = amtThird;
    }

    public Double getAmtFrom() {
        return this.amtFrom;
    }

    public void setAmtFrom(Double amtFrom) {
        this.amtFrom = amtFrom;
    }

    public Double getAmtTo() {
        return this.amtTo;
    }

    public void setAmtTo(Double amtTo) {
        this.amtTo = amtTo;
    }

    public String getCurrencyName() {
        return this.currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getDocumentTypeDesc() {
        return this.documentTypeDesc;
    }

    public void setDocumentTypeDesc(String documentTypeDesc) {
        this.documentTypeDesc = documentTypeDesc;
    }

    public Double getLoanRate() {
        return this.loanRate;
    }

    public void setLoanRate(Double loanRate) {
        this.loanRate = loanRate;
    }

    public Double getWithdrawLeftAmount() {
        return this.withdrawLeftAmount;
    }

    public void setWithdrawLeftAmount(Double withdrawLeftAmount) {
        this.withdrawLeftAmount = withdrawLeftAmount;
    }

    public Double getWithdrawApprovedAmount() {
        return this.withdrawApprovedAmount;
    }

    public void setWithdrawApprovedAmount(Double withdrawApprovedAmount) {
        this.withdrawApprovedAmount = withdrawApprovedAmount;
    }

    public String getWithdrawBankName() {
        return this.withdrawBankName;
    }

    public void setWithdrawBankName(String withdrawBankName) {
        this.withdrawBankName = withdrawBankName;
    }

    public String getWithdrawBankAccountBranchName() {
        return this.withdrawBankAccountBranchName;
    }

    public void setWithdrawBankAccountBranchName(String withdrawBankAccountBranchName) {
        this.withdrawBankAccountBranchName = withdrawBankAccountBranchName;
    }

    public String getCreditContractName() {
        return this.creditContractName;
    }

    public void setCreditContractName(String creditContractName) {
        this.creditContractName = creditContractName;
    }

    public String getCreditContractNumber() {
        return this.creditContractNumber;
    }

    public void setCreditContractNumber(String creditContractNumber) {
        this.creditContractNumber = creditContractNumber;
    }

    public String getCreditLineDesc() {
        return this.creditLineDesc;
    }

    public void setCreditLineDesc(String creditLineDesc) {
        this.creditLineDesc = creditLineDesc;
    }

    public String getRepaymentBankAccountName() {
        return this.repaymentBankAccountName;
    }

    public void setRepaymentBankAccountName(String repaymentBankAccountName) {
        this.repaymentBankAccountName = repaymentBankAccountName;
    }

    public String getRepaymentBankAccountNumber() {
        return this.repaymentBankAccountNumber;
    }

    public void setRepaymentBankAccountNumber(String repaymentBankAccountNumber) {
        this.repaymentBankAccountNumber = repaymentBankAccountNumber;
    }

    public String getWithdrawBankAccountName() {
        return this.withdrawBankAccountName;
    }

    public void setWithdrawBankAccountName(String withdrawBankAccountName) {
        this.withdrawBankAccountName = withdrawBankAccountName;
    }

    public String getWithdrawBankAccountNumber() {
        return this.withdrawBankAccountNumber;
    }

    public void setWithdrawBankAccountNumber(String withdrawBankAccountNumber) {
        this.withdrawBankAccountNumber = withdrawBankAccountNumber;
    }

    public String getBusinessDesc() {
        return this.businessDesc;
    }

    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc;
    }

    public String getContractStatusDesc() {
        return this.contractStatusDesc;
    }

    public void setContractStatusDesc(String contractStatusDesc) {
        this.contractStatusDesc = contractStatusDesc;
    }

    public String getCreditBpName() {
        return this.creditBpName;
    }

    public void setCreditBpName(String creditBpName) {
        this.creditBpName = creditBpName;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return this.contractId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public String getDocumentCategory() {
        return this.documentCategory;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessType() {
        return this.businessType;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractNumber() {
        return this.contractNumber;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractName() {
        return this.contractName;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getContractStatus() {
        return this.contractStatus;
    }

    public void setCreditContractId(Long creditContractId) {
        this.creditContractId = creditContractId;
    }

    public Long getCreditContractId() {
        return this.creditContractId;
    }

    public void setCreditLineId(Long creditLineId) {
        this.creditLineId = creditLineId;
    }

    public Long getCreditLineId() {
        return this.creditLineId;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCreditBpId(Long creditBpId) {
        this.creditBpId = creditBpId;
    }

    public Long getCreditBpId() {
        return this.creditBpId;
    }

    public void setOriginalContractNumber(String originalContractNumber) {
        this.originalContractNumber = originalContractNumber;
    }

    public String getOriginalContractNumber() {
        return this.originalContractNumber;
    }

    public void setFinanceAmount(Double financeAmount) {
        this.financeAmount = financeAmount;
    }

    public Double getFinanceAmount() {
        return this.financeAmount;
    }

    public void setWithdrawBankAccountId(Long withdrawBankAccountId) {
        this.withdrawBankAccountId = withdrawBankAccountId;
    }

    public Long getWithdrawBankAccountId() {
        return this.withdrawBankAccountId;
    }

    public void setRepaymentBankAccountId(Long repaymentBankAccountId) {
        this.repaymentBankAccountId = repaymentBankAccountId;
    }

    public Long getRepaymentBankAccountId() {
        return this.repaymentBankAccountId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
