
package com.hand.hls.fnd.dto;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "fnd_company"
)
@Getter
@Setter
public class FndCompany extends BaseDTO {
    public static final String FIELD_COMPANY_FULL_NAME = "companyFullName";
    public static final String FIELD_COMPANY_CODE = "companyCode";
    public static final String FIELD_COMPANY_ID = "companyId";
    @Id
    @GeneratedValue
    private Long companyId;
    @Condition(
            operator = "LIKE"
    )
    private String companyCode;
    @Condition(
            operator = "LIKE"
    )
    private String companyShortName;
    @Condition(
            operator = "LIKE"
    )
    private String companyFullName;
    private String companyType;
    private String parentCompanyId;
    @Transient
    private String parentCompanyName;
    @Transient
    private String parentCompanyNameN;
    private String businessScope;
    private String address;
    private String zipcode;
    private String fax;
    private String phone;
    private String contactPerson;
    private String enabledFlag;
    private String enterpriseUnifiedCreditCode;
    private String registeredCapital;
    private String legalRepresentative;
    private Date establishmentDate;
    private Date operatingPeriodFrom;
    private Date operatingPeriodTo;
    private String basicAccountBank;
    private String basicAccountNumber;
    private String finCompanyName;

    private String wsComCode;

    @Transient
    private String newCompanyCode;
    @Transient
    private String newCompanyName;
    @Transient
    private String unit;
    @Transient
    private String quarters;
    @Transient
    private String employee;
    @Transient
    private String companyLovName;
    @Transient
    private String companyLovCode;
    @Transient
    private String authorityRuleFlag;

    private String currency;

    @Transient
    private String currencyN;

    @Transient
    private Long setOfBooksId;

    @Transient
    private String setOfBooksIdN;

    @Transient
    private String accountSetIdN;

    @Transient
    private String functionalCurrencyN;


    public FndCompany() {
    }

    public String getWsComCode() {
        return wsComCode;
    }

    public void setWsComCode(String wsComCode) {
        this.wsComCode = wsComCode;
    }

    public String getFinCompanyName() {
        return finCompanyName;
    }

    public void setFinCompanyName(String finCompanyName) {
        this.finCompanyName = finCompanyName;
    }

    public String getParentCompanyNameN() {
        return this.parentCompanyNameN;
    }

    public void setParentCompanyNameN(String parentCompanyNameN) {
        this.parentCompanyNameN = parentCompanyNameN;
    }

    public String getAuthorityRuleFlag() {
        return this.authorityRuleFlag;
    }

    public void setAuthorityRuleFlag(String authorityRuleFlag) {
        this.authorityRuleFlag = authorityRuleFlag;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyLovCode() {
        return this.companyLovCode;
    }

    public void setCompanyLovCode(String companyLovCode) {
        this.companyLovCode = companyLovCode;
    }

    public String getCompanyLovName() {
        return this.companyLovName;
    }

    public void setCompanyLovName(String companyLovName) {
        this.companyLovName = companyLovName;
    }

    public String getUnit() {
        return this.unit;
    }

    public String getCompanyCode() {
        return this.companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyShortName() {
        return this.companyShortName;
    }

    public void setCompanyShortName(String companyShortName) {
        this.companyShortName = companyShortName;
    }

    public String getCompanyFullName() {
        return this.companyFullName;
    }

    public void setCompanyFullName(String companyFullName) {
        this.companyFullName = companyFullName;
    }

    public String getCompanyType() {
        return this.companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getParentCompanyId() {
        return this.parentCompanyId;
    }

    public void setParentCompanyId(String parentCompanyId) {
        this.parentCompanyId = parentCompanyId;
    }

    public String getParentCompanyName() {
        return this.parentCompanyName;
    }

    public void setParentCompanyName(String parentCompanyName) {
        this.parentCompanyName = parentCompanyName;
    }

    public String getBusinessScope() {
        return this.businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return this.zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactPerson() {
        return this.contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnterpriseUnifiedCreditCode() {
        return this.enterpriseUnifiedCreditCode;
    }

    public void setEnterpriseUnifiedCreditCode(String enterpriseUnifiedCreditCode) {
        this.enterpriseUnifiedCreditCode = enterpriseUnifiedCreditCode;
    }

    public String getRegisteredCapital() {
        return this.registeredCapital;
    }

    public void setRegisteredCapital(String registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    public String getLegalRepresentative() {
        return this.legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public Date getEstablishmentDate() {
        return this.establishmentDate;
    }

    public void setEstablishmentDate(Date establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    public Date getOperatingPeriodFrom() {
        return this.operatingPeriodFrom;
    }

    public void setOperatingPeriodFrom(Date operatingPeriodFrom) {
        this.operatingPeriodFrom = operatingPeriodFrom;
    }

    public Date getOperatingPeriodTo() {
        return this.operatingPeriodTo;
    }

    public void setOperatingPeriodTo(Date operatingPeriodTo) {
        this.operatingPeriodTo = operatingPeriodTo;
    }

    public String getBasicAccountBank() {
        return this.basicAccountBank;
    }

    public void setBasicAccountBank(String basicAccountBank) {
        this.basicAccountBank = basicAccountBank;
    }

    public String getBasicAccountNumber() {
        return this.basicAccountNumber;
    }

    public void setBasicAccountNumber(String basicAccountNumber) {
        this.basicAccountNumber = basicAccountNumber;
    }

    public String getNewCompanyCode() {
        return this.newCompanyCode;
    }

    public void setNewCompanyCode(String newCompanyCode) {
        this.newCompanyCode = newCompanyCode;
    }

    public String getNewCompanyName() {
        return this.newCompanyName;
    }

    public void setNewCompanyName(String newCompanyName) {
        this.newCompanyName = newCompanyName;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQuarters() {
        return this.quarters;
    }

    public void setQuarters(String quarters) {
        this.quarters = quarters;
    }

    public String getEmployee() {
        return this.employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    private String bankAccountId;
    @Transient
    private String bankAccountName;
    @Transient
    private String bankAccountNum;
    @Transient
    private String bankBranchName;


    public String getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(String bankAccountId) {
        this.bankAccountId = bankAccountId;
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

    public String getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyN() {
        return currencyN;
    }

    public void setCurrencyN(String currencyN) {
        this.currencyN = currencyN;
    }
}
