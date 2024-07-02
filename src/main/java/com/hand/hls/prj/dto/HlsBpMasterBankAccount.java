//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Table(
    name = "hls_bp_master_bank_account"
)
@Getter
@Setter
public class HlsBpMasterBankAccount extends BaseDTO {
    @Id
    @Column(
        name = "BANK_ACCOUNT_ID"
    )
    @GeneratedValue
    private Long bankAccountId;
    @Column(
        name = "BP_ID"
    )
    private Long bpId;
    @Column(
        name = "BANK_ACCOUNT_NUM"
    )
    private String bankAccountNum;
    @Column(
        name = "BANK_ACCOUNT_CODE"
    )
    private String bankAccountCode;
    @Column(
        name = "BANK_ACCOUNT_NAME"
    )
    private String bankAccountName;
    @Column(
        name = "CURRENCY"
    )
    private String currency;
    @Column(
        name = "ENABLED_FLAG"
    )
    private String enabledFlag;
    @Column(
        name = "BANK_FULL_NAME"
    )
    private String bankFullName;
    @Column(
        name = "BANK_BRANCH_NAME"
    )
    private String bankBranchName;
    @Column(
        name = "BANK_BRANCH_ID"
    )
    private Long bankBranchId;
    @Column(
        name = "COUNTRY_ID"
    )
    private Long countryId;
    @Column(
        name = "PROVINCE_ID"
    )
    private Long provinceId;
    @Column(
        name = "CITY_ID"
    )
    private Long cityId;
    @Column(
        name = "DISTRICT_ID"
    )
    private Long districtId;
    @Column(
        name = "CREATED_BY"
    )
    private Long createdBy;
    @Column(
        name = "CREATION_DATE"
    )
    private Date creationDate;
    @Column(
        name = "LAST_UPDATED_BY"
    )
    private Long lastUpdatedBy;
    @Column(
        name = "LAST_UPDATE_DATE"
    )
    private Date lastUpdateDate;
    @Column(
        name = "REF_V01"
    )
    private String refV01;
    @Column(
        name = "REF_V02"
    )
    private String refV02;
    @Column(
        name = "REF_V03"
    )
    private String refV03;
    @Column(
        name = "REF_V04"
    )
    private String refV04;
    @Column(
        name = "REF_V05"
    )
    private String refV05;
    @Column(
        name = "REF_N01"
    )
    private Long refN01;
    @Column(
        name = "REF_N02"
    )
    private Long refN02;
    @Column(
        name = "REF_N03"
    )
    private Long refN03;
    @Column(
        name = "REF_N04"
    )
    private Long refN04;
    @Column(
        name = "REF_N05"
    )
    private Long refN05;
    @Column(
        name = "REF_D01"
    )
    private Date refD01;
    @Column(
        name = "REF_D02"
    )
    private Date refD02;
    @Column(
        name = "REF_D03"
    )
    private Date refD03;
    @Column(
        name = "REF_D04"
    )
    private Date refD04;
    @Column(
        name = "REF_D05"
    )
    private Date refD05;
    @Column(
        name = "LOAN_ACCOUNT"
    )
    private String loanAccount;
    @Column(
        name = "PAYMENT_ACCOUNT"
    )
    private String paymentAccount;
    @Column(
        name = "BALANCE_ACCOUNT"
    )
    private String balanceAccount;
    @Column(
        name = "BANK_ACCOUNT_APPLICATION"
    )
    private String bankAccountApplication;
    @Column(
        name = "TSS_BANK_CODE"
    )
    private String tssBankCode;
    @Column(
        name = "TSS_BANK_NUM"
    )
    private String tssBankNum;
    @Column(
        name = "ACCOUNT_NATURE"
    )
    private String accountNature;
    @Column(
        name = "CNAPS_CODE"
    )
    private String cnapsCode;
    @Column(
        name = "SWIFT_CODE"
    )
    private String swiftCode;
    @Column(
        name = "ACCOUNT_START_DATE"
    )
    private String accountStartDate;
    @Column(
        name = "ACCOUNT_END_DATE"
    )
    private String accountEndDate;
    @Transient
    private String tssBankCodeN;
    @Transient
    private String countryIdN;
    @Transient
    private String provinceIdN;
    @Transient
    private String cityIdN;
    @Transient
    private String districtIdN;
    @Transient
    private String currencyN;
    @Transient
    private String accountNatureN;

    private String country;
    private String province;
    private String city;
    private String district;
    private String debtFlag;
    private Long debtBankId;

    @Transient
    private String debtBankName;
    @Transient
    private String bpCode;
    @Transient
    private String debtBankCode;

    private String ebsBusinessCode;

    public HlsBpMasterBankAccount() {
    }

    public String getDistrictIdN() {
        return this.districtIdN;
    }

    public void setDistrictIdN(String districtIdN) {
        this.districtIdN = districtIdN;
    }

    public String getTssBankCodeN() {
        return this.tssBankCodeN;
    }

    public void setTssBankCodeN(String tssBankCodeN) {
        this.tssBankCodeN = tssBankCodeN;
    }

    public String getCountryIdN() {
        return this.countryIdN;
    }

    public void setCountryIdN(String countryIdN) {
        this.countryIdN = countryIdN;
    }

    public String getProvinceIdN() {
        return this.provinceIdN;
    }

    public void setProvinceIdN(String provinceIdN) {
        this.provinceIdN = provinceIdN;
    }

    public String getCityIdN() {
        return this.cityIdN;
    }

    public void setCityIdN(String cityIdN) {
        this.cityIdN = cityIdN;
    }

    public Long getBankAccountId() {
        return this.bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getBankAccountNum() {
        return this.bankAccountNum;
    }

    public void setBankAccountNum(String bankAccountNum) {
        this.bankAccountNum = bankAccountNum;
    }

    public String getBankAccountCode() {
        return this.bankAccountCode;
    }

    public void setBankAccountCode(String bankAccountCode) {
        this.bankAccountCode = bankAccountCode;
    }

    public String getBankAccountName() {
        return this.bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getBankFullName() {
        return this.bankFullName;
    }

    public void setBankFullName(String bankFullName) {
        this.bankFullName = bankFullName;
    }

    public String getBankBranchName() {
        return this.bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public Long getBankBranchId() {
        return this.bankBranchId;
    }

    public void setBankBranchId(Long bankBranchId) {
        this.bankBranchId = bankBranchId;
    }

    public Long getCountryId() {
        return this.countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getProvinceId() {
        return this.provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return this.cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getDistrictId() {
        return this.districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getRefV01() {
        return this.refV01;
    }

    public void setRefV01(String refV01) {
        this.refV01 = refV01;
    }

    public String getRefV02() {
        return this.refV02;
    }

    public void setRefV02(String refV02) {
        this.refV02 = refV02;
    }

    public String getRefV03() {
        return this.refV03;
    }

    public void setRefV03(String refV03) {
        this.refV03 = refV03;
    }

    public String getRefV04() {
        return this.refV04;
    }

    public void setRefV04(String refV04) {
        this.refV04 = refV04;
    }

    public String getRefV05() {
        return this.refV05;
    }

    public void setRefV05(String refV05) {
        this.refV05 = refV05;
    }

    public Long getRefN01() {
        return this.refN01;
    }

    public void setRefN01(Long refN01) {
        this.refN01 = refN01;
    }

    public Long getRefN02() {
        return this.refN02;
    }

    public void setRefN02(Long refN02) {
        this.refN02 = refN02;
    }

    public Long getRefN03() {
        return this.refN03;
    }

    public void setRefN03(Long refN03) {
        this.refN03 = refN03;
    }

    public Long getRefN04() {
        return this.refN04;
    }

    public void setRefN04(Long refN04) {
        this.refN04 = refN04;
    }

    public Long getRefN05() {
        return this.refN05;
    }

    public void setRefN05(Long refN05) {
        this.refN05 = refN05;
    }

    public Date getRefD01() {
        return this.refD01;
    }

    public void setRefD01(Date refD01) {
        this.refD01 = refD01;
    }

    public Date getRefD02() {
        return this.refD02;
    }

    public void setRefD02(Date refD02) {
        this.refD02 = refD02;
    }

    public Date getRefD03() {
        return this.refD03;
    }

    public void setRefD03(Date refD03) {
        this.refD03 = refD03;
    }

    public Date getRefD04() {
        return this.refD04;
    }

    public void setRefD04(Date refD04) {
        this.refD04 = refD04;
    }

    public Date getRefD05() {
        return this.refD05;
    }

    public void setRefD05(Date refD05) {
        this.refD05 = refD05;
    }

    public String getLoanAccount() {
        return this.loanAccount;
    }

    public void setLoanAccount(String loanAccount) {
        this.loanAccount = loanAccount;
    }

    public String getPaymentAccount() {
        return this.paymentAccount;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public String getBalanceAccount() {
        return this.balanceAccount;
    }

    public void setBalanceAccount(String balanceAccount) {
        this.balanceAccount = balanceAccount;
    }

    public String getBankAccountApplication() {
        return this.bankAccountApplication;
    }

    public void setBankAccountApplication(String bankAccountApplication) {
        this.bankAccountApplication = bankAccountApplication;
    }

    public String getTssBankCode() {
        return this.tssBankCode;
    }

    public void setTssBankCode(String tssBankCode) {
        this.tssBankCode = tssBankCode;
    }

    public String getTssBankNum() {
        return this.tssBankNum;
    }

    public void setTssBankNum(String tssBankNum) {
        this.tssBankNum = tssBankNum;
    }

    public String getAccountNature() {
        return this.accountNature;
    }

    public void setAccountNature(String accountNature) {
        this.accountNature = accountNature;
    }

    public String getCnapsCode() {
        return this.cnapsCode;
    }

    public void setCnapsCode(String cnapsCode) {
        this.cnapsCode = cnapsCode;
    }

    public String getSwiftCode() {
        return this.swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public String getAccountStartDate() {
        return this.accountStartDate;
    }

    public void setAccountStartDate(String accountStartDate) {
        this.accountStartDate = accountStartDate;
    }

    public String getAccountEndDate() {
        return this.accountEndDate;
    }

    public void setAccountEndDate(String accountEndDate) {
        this.accountEndDate = accountEndDate;
    }

    public String getCurrencyN() {
        return this.currencyN;
    }

    public void setCurrencyN(String currencyN) {
        this.currencyN = currencyN;
    }

    public String getAccountNatureN() {
        return this.accountNatureN;
    }

    public void setAccountNatureN(String accountNatureN) {
        this.accountNatureN = accountNatureN;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district == null ? null : district.trim();
    }

    public String getDebtFlag() {
        return this.debtFlag;
    }

    public void setDebtFlag(String debtFlag) {
        this.debtFlag = debtFlag;
    }

    public Long getDebtBankId() {
        return this.debtBankId;
    }

    public void setDebtBankId(Long debtBankId) {
        this.debtBankId = debtBankId;
    }

    public String getBpCode() {
        return this.bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getDebtBankCode() {
        return this.debtBankCode;
    }

    public void setDebtBankCode(String debtBankCode) {
        this.debtBankCode = debtBankCode;
    }

    public String getDebtBankName() {
        return this.debtBankName;
    }

    public void setDebtBankName(String debtBankName) {
        this.debtBankName = debtBankName;
    }
}
