//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "hls_bp_master"
)
public class HlsBpMaster extends BaseDTO {
    public String getDescription() {
        return description;
    }

    public String getEconomicSectorN() {
        return economicSectorN;
    }

    public void setEconomicSectorN(String economicSectorN) {
        this.economicSectorN = economicSectorN;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActualExpirationDate() {
        return actualExpirationDate;
    }

    public void setActualExpirationDate(String actualExpirationDate) {
        this.actualExpirationDate = actualExpirationDate;
    }

    @Id
    @Column(
            name = "BP_ID"
    )
    private Long bpId;
    private String holdingType;

    public String getHoldingType() {
        return holdingType;
    }

    public void setHoldingType(String holdingType) {
        this.holdingType = holdingType;
    }

    public String getThreeCertificates() {
        return threeCertificates;
    }

    public void setThreeCertificates(String threeCertificates) {
        this.threeCertificates = threeCertificates;
    }

    private String skyFlag;
    @Length(max = 30)
    private String associatedDivision; //关联区分
    @Length(max = 30)
    private String threeCertificates; //是否三证合一

    private Date operatingPeriodStart; //营业期限自
    private String businessArea;
    @Transient
    private String shareholderName; //股东名称

    public String getShareholderName() {
        return shareholderName;
    }

    public void setShareholderName(String shareholderName) {
        this.shareholderName = shareholderName;
    }

    @Length(max = 100)
    private String environmentalLevel; //环保等级



    private Double liabilities; //或有负债
    private String enterpriseAffiliation;

    public String getEnterpriseAffiliation() {
        return enterpriseAffiliation;
    }

    public void setEnterpriseAffiliation(String enterpriseAffiliation) {
        this.enterpriseAffiliation = enterpriseAffiliation;
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    @Length(max = 1000)
    private String situationDescription; //其他情况说明

    @Length(max = 1000)
    private String mainExperience; //主要经历

    @Length(max = 100)
    private String familyName; //家庭成员姓名

    @Length(max = 100)
    private String familyGender; //家庭成员性别

    @Length(max = 100)
    private String familyRelationship; //与客户关系

    @Length(max = 100)
    private String familyAcademicBackground; //家庭成员学历

    @Length(max = 100)
    private String familyIdType; //家庭成员证件类型

    @Length(max = 100)
    private String familyIdCardNo; //家庭成员证件号码

    private String familyIdAddress; //家庭成员身份证地址

    private Date familyDateOfBirth; //家庭成员出生日期

    @Length(max = 255)
    private String familyContactInformation; //家庭成员联系方式

    @Length(max = 255)
    private String familyContactAddress; //家庭成员联系地址

    @Length(max = 30)
    private String domesticOversea; //境内境外

    @Length(max = 30)
    private String localNonlocal; //本地外地

    private String orgType;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getSaveFlag() {
        return saveFlag;
    }

    public void setSaveFlag(String saveFlag) {
        this.saveFlag = saveFlag;
    }

    public String getInterStatus() {
        return interStatus;
    }

    public void setInterStatus(String interStatus) {
        this.interStatus = interStatus;
    }

    public String getInterInfo() {
        return interInfo;
    }

    public void setInterInfo(String interInfo) {
        this.interInfo = interInfo;
    }

    public String getFirstLoanAccount() {
        return firstLoanAccount;
    }

    public void setFirstLoanAccount(String firstLoanAccount) {
        this.firstLoanAccount = firstLoanAccount;
    }

    public String getEconomicSector() {
        return economicSector;
    }

    public void setEconomicSector(String economicSector) {
        this.economicSector = economicSector;
    }

    public Double getBpFactor() {
        return bpFactor;
    }

    public void setBpFactor(Double bpFactor) {
        this.bpFactor = bpFactor;
    }

    public String getAgencyCreditCode() {
        return agencyCreditCode;
    }

    public void setAgencyCreditCode(String agencyCreditCode) {
        this.agencyCreditCode = agencyCreditCode;
    }

    public String getAccountLicenseNum() {
        return accountLicenseNum;
    }

    public void setAccountLicenseNum(String accountLicenseNum) {
        this.accountLicenseNum = accountLicenseNum;
    }

    public String getEconomicInduClassSec() {
        return economicInduClassSec;
    }

    public void setEconomicInduClassSec(String economicInduClassSec) {
        this.economicInduClassSec = economicInduClassSec;
    }

    public String getEconomicInduClassThr() {
        return economicInduClassThr;
    }

    public void setEconomicInduClassThr(String economicInduClassThr) {
        this.economicInduClassThr = economicInduClassThr;
    }

    public String getInternalIndu() {
        return internalIndu;
    }

    public void setInternalIndu(String internalIndu) {
        this.internalIndu = internalIndu;
    }

    public String getHighRiskIndu() {
        return highRiskIndu;
    }

    public void setHighRiskIndu(String highRiskIndu) {
        this.highRiskIndu = highRiskIndu;
    }

    public String getCreditCurrency() {
        return creditCurrency;
    }

    public void setCreditCurrency(String creditCurrency) {
        this.creditCurrency = creditCurrency;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * 公司ID
     */
    private Long companyId;

    private String saveFlag;

    private String interStatus;

    private String interInfo;
    private String firstLoanAccount;
    private String economicSector;
    @Transient
    private String economicSectorN;


    /**
     * 客户系数
     */
    private Double bpFactor;

    public Long getChangeReqId() {
        return changeReqId;
    }

    public void setChangeReqId(Long changeReqId) {
        this.changeReqId = changeReqId;
    }

    private String agencyCreditCode;//机构信用代码


    private String dataClass;
    private Long changeReqId;

    public String getDataClass() {
        return dataClass;
    }

    public void setDataClass(String dataClass) {
        this.dataClass = dataClass;
    }

    private String accountLicenseNum;//开户许可证核准号





    private String economicInduClassSec;//经济行业分类（二级联动）

    private String economicInduClassThr;//经济行业分类（三级联动）

    private String internalIndu;//内部行业

    private String highRiskIndu;//是否高风险行业







    private String creditCurrency;
    private Date validFrom;
    private Date validTo;


    private Double creditLineAmount;
    private String creditType;

    public String getAssociatedDivision() {
        return associatedDivision;
    }

    public Date getOperatingPeriodStart() {
        return operatingPeriodStart;
    }

    public void setOperatingPeriodStart(Date operatingPeriodStart) {
        this.operatingPeriodStart = operatingPeriodStart;
    }

    public String getEnvironmentalLevel() {
        return environmentalLevel;
    }

    public void setEnvironmentalLevel(String environmentalLevel) {
        this.environmentalLevel = environmentalLevel;
    }

    public Double getLiabilities() {
        return liabilities;
    }

    public void setLiabilities(Double liabilities) {
        this.liabilities = liabilities;
    }

    public String getSituationDescription() {
        return situationDescription;
    }

    public void setSituationDescription(String situationDescription) {
        this.situationDescription = situationDescription;
    }

    public String getMainExperience() {
        return mainExperience;
    }

    public void setMainExperience(String mainExperience) {
        this.mainExperience = mainExperience;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyGender() {
        return familyGender;
    }

    public void setFamilyGender(String familyGender) {
        this.familyGender = familyGender;
    }

    public String getFamilyRelationship() {
        return familyRelationship;
    }

    public void setFamilyRelationship(String familyRelationship) {
        this.familyRelationship = familyRelationship;
    }

    public String getFamilyAcademicBackground() {
        return familyAcademicBackground;
    }

    public void setFamilyAcademicBackground(String familyAcademicBackground) {
        this.familyAcademicBackground = familyAcademicBackground;
    }

    public String getFamilyIdType() {
        return familyIdType;
    }

    public void setFamilyIdType(String familyIdType) {
        this.familyIdType = familyIdType;
    }

    public String getFamilyIdCardNo() {
        return familyIdCardNo;
    }

    public void setFamilyIdCardNo(String familyIdCardNo) {
        this.familyIdCardNo = familyIdCardNo;
    }

    public String getFamilyIdAddress() {
        return familyIdAddress;
    }

    public void setFamilyIdAddress(String familyIdAddress) {
        this.familyIdAddress = familyIdAddress;
    }

    public Date getFamilyDateOfBirth() {
        return familyDateOfBirth;
    }

    public void setFamilyDateOfBirth(Date familyDateOfBirth) {
        this.familyDateOfBirth = familyDateOfBirth;
    }

    public String getFamilyContactInformation() {
        return familyContactInformation;
    }

    public void setFamilyContactInformation(String familyContactInformation) {
        this.familyContactInformation = familyContactInformation;
    }

    public String getFamilyContactAddress() {
        return familyContactAddress;
    }

    public void setFamilyContactAddress(String familyContactAddress) {
        this.familyContactAddress = familyContactAddress;
    }

    public String getDomesticOversea() {
        return domesticOversea;
    }

    public void setDomesticOversea(String domesticOversea) {
        this.domesticOversea = domesticOversea;
    }

    public String getLocalNonlocal() {
        return localNonlocal;
    }

    public void setLocalNonlocal(String localNonlocal) {
        this.localNonlocal = localNonlocal;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public Double getCreditLineAmount() {
        return creditLineAmount;
    }

    public void setCreditLineAmount(Double creditLineAmount) {
        this.creditLineAmount = creditLineAmount;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public String getCompanyIndustry() {
        return companyIndustry;
    }

    public void setCompanyIndustry(String companyIndustry) {
        this.companyIndustry = companyIndustry;
    }

    public String getBusinessJob() {
        return businessJob;
    }

    public void setBusinessJob(String businessJob) {
        this.businessJob = businessJob;
    }

    public Long getWorkingYears() {
        return workingYears;
    }

    public void setWorkingYears(Long workingYears) {
        this.workingYears = workingYears;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getSapSendVenderFlag() {
        return sapSendVenderFlag;
    }

    public void setSapSendVenderFlag(String sapSendVenderFlag) {
        this.sapSendVenderFlag = sapSendVenderFlag;
    }

    public String getSapSendBpFlag() {
        return sapSendBpFlag;
    }

    public void setSapSendBpFlag(String sapSendBpFlag) {
        this.sapSendBpFlag = sapSendBpFlag;
    }

    public void setAssociatedDivision(String associatedDivision) {
        this.associatedDivision = associatedDivision;
    }

    @Transient
    private String bpCodeFromN;
    @Transient
    private String assetTypeN;
    @Transient
    private String bpCodeToN;

    public String getCompanyInformation() {
        return companyInformation;
    }

    public void setCompanyInformation(String companyInformation) {
        this.companyInformation = companyInformation;
    }

    private String companyInformation;
    private Date registeredDate;
    private String nativePlace;

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    private String loanCardPassword;

    public String getLoanCardPassword() {
        return loanCardPassword;
    }

    public void setLoanCardPassword(String loanCardPassword) {
        this.loanCardPassword = loanCardPassword;
    }

    public String getEnterpriseUnifiedCreditCode() {
        return enterpriseUnifiedCreditCode;
    }

    public void setEnterpriseUnifiedCreditCode(String enterpriseUnifiedCreditCode) {
        this.enterpriseUnifiedCreditCode = enterpriseUnifiedCreditCode;
    }

    private String enterpriseUnifiedCreditCode;

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getSkyFlag() {
        return skyFlag;
    }

    public void setSkyFlag(String skyFlag) {
        this.skyFlag = skyFlag;
    }

    @Transient
    private String existFlag;
    private String workingCompany;

    private String companyIndustry;
    private String businessJob;
    private Long workingYears;
    private String groupCode;

    private String sapSendVenderFlag;
    private String sapSendBpFlag;
    @Transient
    private String unitIdN;
    @Transient
    private String creationDateN;
    @Transient
    private String bpApproveStatusN;
    @Transient
    private String projectId;
    @Transient
    private String projectNumber;
    @Transient
    private String projectName;
    @Transient
    private String contractId;
    @Transient
    private String contractNumber;
    @Transient
    private String contractName;
    @Transient
    private String unitId;
    @Column(
            name = "OWNER_USER_ID"
    )
    private Long ownerUserId;
    @Transient
    private String ownerUserIdN;
    @Column(
            name = "BP_CODE"
    )
    private String bpCode;
    @Column(
            name = "BP_NAME"
    )
    private String bpName;
    @Column(
            name = "EXTRA_NAM"
    )
    private String extraNam;
    @Column(
            name = "BP_CLASS"
    )
    private String bpClass;
    @Transient
    private String bpClassN;
    @Column(
            name = "BP_CATEGORY"
    )
    private String bpCategory;
    @Column(
            name = "BP_TYPE"
    )
    private String bpType;
    @Transient
    private String bpTypeN;
    @Column(
            name = "BP_TITLE"
    )
    private String bpTitle;
    @Column(
            name = "SEARCH_TERM_1"
    )
    private String searchTerm1;
    @Column(
            name = "SEARCH_TERM_2"
    )
    private Long searchTerm2;
    @Column(
            name = "EXTERNAL_BP_CODE"
    )
    private String externalBpCode;
    @Column(
            name = "ADDRESS_ID"
    )
    private Long addressId;
    @Column(
            name = "ENABLED_FLAG"
    )
    private String enabledFlag;
    @Column(
            name = "FIRST_NAME"
    )
    private String firstName;
    @Column(
            name = "MIDDLE_NAME"
    )
    private String middleName;
    @Column(
            name = "LAST_NAME"
    )
    private String lastName;
    @Column(
            name = "GENDER"
    )
    private String gender;
    @Column(
            name = "NATIONALITY"
    )
    private String nationality;
    @Column(
            name = "DATE_OF_BIRTH"
    )
    private Date dateOfBirth;
    @Column(
            name = "PLACE_OF_BIRTH"
    )
    private String placeOfBirth;
    @Column(
            name = "NAME_AT_BIRTH"
    )
    private String nameAtBirth;
    @Column(
            name = "MARITAL_STATUS"
    )
    private String maritalStatus;
    @Column(
            name = "NUMBER_OF_CHILDREN"
    )
    private Long numberOfChildren;
    @Column(
            name = "ACADEMIC_BACKGROUND"
    )
    private String academicBackground;
    @Column(
            name = "AGE"
    )
    private Long age;
    @Column(
            name = "ID_TYPE"
    )
    private String idType;
    @Column(
            name = "ID_CARD_NO"
    )
    private String idCardNo;
    @Column(
            name = "ANNUAL_INCOME"
    )
    private Long annualIncome;
    @Column(
            name = "CURRENCY"
    )
    private String currency;
    @Column(
            name = "CAPITAL_OF_FAMILY"
    )
    private Long capitalOfFamily;
    @Column(
            name = "LIABILITY_OF_FAMILY"
    )
    private Long liabilityOfFamily;
    @Column(
            name = "LEGAL_FORM"
    )
    private String legalForm;
    @Column(
            name = "INDUSTRY"
    )
    private String industry;
    @Column(
            name = "BUSINESS_LICENSE_NUM"
    )
    private String businessLicenseNum;
    @Column(
            name = "CORPORATE_CODE"
    )
    private String corporateCode;
    @Column(
            name = "ORGANIZATION_CODE"
    )
    private String organizationCode;
    @Column(
            name = "TAX_REGISTRY_NUM"
    )
    private String taxRegistryNum;
    @Column(
            name = "REGISTERED_PLACE"
    )
    private String registeredPlace;
    @Column(
            name = "FOUNDED_DATE"
    )
    private Date foundedDate;
    @Column(
            name = "REGISTERED_CAPITAL"
    )
    private Long registeredCapital;
    @Column(
            name = "BALANCE_SHEET_CURRENCY"
    )
    private String balanceSheetCurrency;
    @Column(
            name = "TAXPAYER_TYPE"
    )
    private String taxpayerType;
    @Column(
            name = "INVOICE_TITLE"
    )
    private String invoiceTitle;
    @Column(
            name = "INVOICE_BP_ADDRESS_PHONE_NUM"
    )
    private String invoiceBpAddressPhoneNum;
    @Column(
            name = "INVOICE_BP_BANK_ACCOUNT"
    )
    private String invoiceBpBankAccount;
    @Column(
            name = "LOAN_CARD_NUM"
    )
    private String loanCardNum;
    @Column(
            name = "PAID_UP_CAPITAL"
    )
    private String paidUpCapital;
    @Column(
            name = "COMPANY_NATURE"
    )
    private String companyNature;
    @Column(
            name = "PRIMARY_BUSINESS"
    )
    private String primaryBusiness;
    @Column(
            name = "MAIN_PRODUCTS"
    )
    private String mainProducts;
    @Column(
            name = "BP_NAME_SP"
    )
    private String bpNameSp;
    @Transient
    private String actualController;
    @Column(
            name = "GENDER_SP"
    )
    private String genderSp;
    @Column(
            name = "DATE_OF_BIRTH_SP"
    )
    private Date dateOfBirthSp;
    @Column(
            name = "ACADEMIC_BACKGROUND_SP"
    )
    private String academicBackgroundSp;
    @Column(
            name = "ID_TYPE_SP"
    )
    private String idTypeSp;
    @Column(
            name = "ID_CARD_NO_SP"
    )
    private String idCardNoSp;
    @Column(
            name = "COUNTRY_SP"
    )
    private String countrySp;
    @Column(
            name = "PROVINCE_SP"
    )
    private String provinceSp;
    @Column(
            name = "CITY_SP"
    )
    private String citySp;
    @Column(
            name = "DISTRICT_SP"
    )
    private String districtSp;
    @Column(
            name = "ADDRESS_SP"
    )
    private String addressSp;
    @Column(
            name = "CREATED_BY"
    )
    private Long createdBy;

    @Column(
            name = "LAST_UPDATED_BY"
    )
    private Long lastUpdatedBy;
    @Column(
            name = "LAST_UPDATETIME_DATE"
    )
    private Date lastUpdatetimeDate;
    @Column(
            name = "LAST_UPDATE_DATE"
    )
    private Date lastUpdateDate;
    @Column(
            name = "CREATION_DATE"
    )
    private Date creationDate;
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
            name = "REF_V06"
    )
    private String refV06;
    @Column(
            name = "REF_V07"
    )
    private String refV07;
    @Column(
            name = "REF_V08"
    )
    private String refV08;
    @Column(
            name = "REF_V09"
    )
    private String refV09;
    @Column(
            name = "REF_V10"
    )
    private String refV10;
    @Column(
            name = "REF_V11"
    )
    private String refV11;
    @Column(
            name = "REF_V12"
    )
    private String refV12;
    @Column(
            name = "REF_V13"
    )
    private String refV13;
    @Column(
            name = "REF_V14"
    )
    private String refV14;
    @Column(
            name = "REF_V15"
    )
    private String refV15;
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
            name = "REF_N06"
    )
    private Long refN06;
    @Column(
            name = "REF_N07"
    )
    private Long refN07;
    @Column(
            name = "REF_N08"
    )
    private Long refN08;
    @Column(
            name = "REF_N09"
    )
    private Long refN09;
    @Column(
            name = "REF_N10"
    )
    private Long refN10;
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
            name = "ENTERPRISE_SCALE"
    )
    private String enterpriseScale;
    @Column(
            name = "LEGAL_PERSON"
    )
    private String legalPerson;
    @Column(
            name = "SPOUSE_PHONE"
    )
    private Long spousePhone;
    @Column(
            name = "LICENSE_TERMS"
    )
    private Date licenseTerms;
    @Column(
            name = "PORPORTION_OF_GUARANTEE"
    )
    private Long porportionOfGuarantee;
    @Column(
            name = "PERIOD_IN_JOB"
    )
    private String periodInJob;
    @Column(
            name = "NET_MONTHLY_INCOME"
    )
    private Long netMonthlyIncome;
    @Column(
            name = "LEADER_FLAG"
    )
    private String leaderFlag;
    @Column(
            name = "MORTGAGE_FLAG"
    )
    private String mortgageFlag;
    @Column(
            name = "LOCAL_PERSON_FLAG"
    )
    private String localPersonFlag;
    @Column(
            name = "HIGH_MENTAL_FLAG"
    )
    private String highMentalFlag;
    @Column(
            name = "HAS_HOUSE_FLAG"
    )
    private String hasHouseFlag;
    @Column(
            name = "HAS_CAR_FLAG"
    )
    private String hasCarFlag;
    @Column(
            name = "COMMUNITY_LEADER_FLAG"
    )
    private String communityLeaderFlag;
    @Column(
            name = "BANK_MATCH_FLAG"
    )
    private String bankMatchFlag;
    @Column(
            name = "HOUSE_PROPERTY_VALUE"
    )
    private Long housePropertyValue;
    @Column(
            name = "HOUSE_LOAN_BALANCE"
    )
    private Long houseLoanBalance;
    @Column(
            name = "DEPOSIT_CERTIFICATE"
    )
    private String depositCertificate;
    @Column(
            name = "CREDIT_CARD_LIMIT"
    )
    private String creditCardLimit;
    @Column(
            name = "ADDRESS_ON_ID"
    )
    private String addressOnId;
    @Column(
            name = "ADDRESS_ON_RESIDENT_BOOKLIT"
    )
    private String addressOnResidentBooklit;
    @Column(
            name = "LIVING_ADDRESS"
    )
    private String livingAddress;
    @Column(
            name = "WORKING_PLACE"
    )
    private String workingPlace;
    @Column(
            name = "WORKING_DURATION"
    )
    private String workingDuration;
    @Column(
            name = "WORKING_ADDRESS"
    )
    private String workingAddress;
    @Column(
            name = "OPERATION_YEAR"
    )
    private String operationYear;
    @Column(
            name = "POSITION"
    )
    private String position;
    @Column(
            name = "CELL_PHONE"
    )
    private String cellPhone;
    @Column(
            name = "PHONE_EXTRA"
    )
    private String phoneExtra;
    @Column(
            name = "PHONE"
    )
    private String phone;
    @Column(
            name = "CONTACT_PERSON"
    )
    private String contactPerson;
    @Column(
            name = "FAX_NUMBER"
    )
    private String faxNumber;
    @Column(
            name = "DEPARTMENT"
    )
    private String department;
    @Column(
            name = "CREDIT_FLAG"
    )
    private String creditFlag;
    @Column(
            name = "CREDIT_AMOUNT"
    )
    private Long creditAmount;
    @Column(
            name = "CREDIT_ALT"
    )
    private Long creditAlt;
    @Column(
            name = "CREDIT_FORBID"
    )
    private Long creditForbid;
    @Column(
            name = "EMAIL"
    )
    private String email;
    @Column(
            name = "NC_STATUS"
    )
    private String ncStatus;
    @Column(
            name = "CELL_PHONE_2"
    )
    private String cellPhone2;
    @Column(
            name = "EMPLOYEE_AMOUNT"
    )
    private Long employeeAmount;
    @Column(
            name = "BILLING_STATUS"
    )
    private String billingStatus;
    @Column(
            name = "FIN_NET_CASH_INFLOW"
    )
    private Long finNetCashInflow;
    @Column(
            name = "FIN_MONTHLY_PAYMENT"
    )
    private Long finMonthlyPayment;
    @Column(
            name = "FIN_MONTHS"
    )
    private Long finMonths;
    @Column(
            name = "FIN_LIQUIDITY_RATIO"
    )
    private String finLiquidityRatio;
    @Column(
            name = "FIN_LEVERAGE"
    )
    private String finLeverage;
    @Column(
            name = "FIN_DATA"
    )
    private String finData;
    @Column(
            name = "FIN_EVALUATION"
    )
    private String finEvaluation;
    @Column(
            name = "CDD_LIST_ID"
    )
    private Long cddListId;
    @Column(
            name = "PB_NUMBER"
    )
    private String pbNumber;
    @Column(
            name = "REPO_NUMBER"
    )
    private String repoNumber;
    @Column(
            name = "BLACK_FLAG"
    )
    private String blackFlag;
    @Column(
            name = "LOCK_FLAG"
    )
    private String lockFlag;
    @Column(
            name = "OLD_FLAG"
    )
    private String oldFlag;
    @Column(
            name = "LIMIT_FROM"
    )
    private Date limitFrom;
    @Column(
            name = "LIMIT_TO"
    )
    private Date limitTo;
    @Column(
            name = "LAW_BP_FLAG"
    )
    private String lawBpFlag;
    @Column(
            name = "FIN_TURNOVER_1"
    )
    private String finTurnover1;
    @Column(
            name = "IF_TO_ZX_FLAG"
    )
    private String ifToZxFlag;
    @Column(
            name = "ZX_LAST_UPDATE_DATE"
    )
    private Date zxLastUpdateDate;
    @Column(
            name = "ZX_LAST_UPDATED_BY"
    )
    private Long zxLastUpdatedBy;
    @Column(
            name = "CREDIT_AMOUNT_USED_FIX"
    )
    private Long creditAmountUsedFix;
    @Column(
            name = "CREDIT_AMOUNT_USED_CYCLE"
    )
    private Long creditAmountUsedCycle;
    @Column(
            name = "CREDIT_AMOUNT_FROZEN_FIX"
    )
    private Long creditAmountFrozenFix;
    @Column(
            name = "CREDIT_AMOUNT_FROZEN_CYCLE"
    )
    private Long creditAmountFrozenCycle;
    @Column(
            name = "POLLING_TIMES"
    )
    private Long pollingTimes;
    @Column(
            name = "SALE_TYPE"
    )
    private String saleType;
    @Column(
            name = "ASSET_TYPE"
    )
    private String assetType;
    @Column(
            name = "AREA"
    )
    private String area;
    @Column(
            name = "LISTED_COMPANY_FLAG"
    )
    private String listedCompanyFlag;
    @Column(
            name = "MARKET_LOCATION"
    )
    private String marketLocation;
    @Column(
            name = "CREDIT_RATING"
    )
    private String creditRating;
    @Column(
            name = "RATING_DATE"
    )
    private Date ratingDate;
    @Column(
            name = "INVOICE_KIND"
    )
    private String invoiceKind;
    @Column(
            name = "LICENSE_TERMS_IF_LONG"
    )
    private String licenseTermsIfLong;
    @Column(
            name = "LISTED_SUBJECT"
    )
    private String listedSubject;
    @Column(
            name = "LISTED_SUBJECT_ORG_CODE"
    )
    private String listedSubjectOrgCode;
    @Column(
            name = "ACTUAL_CONTROLLER_ID_TYPE"
    )
    private String actualControllerIdType;
    @Column(
            name = "ACTUAL_CONTROLLER_ID"
    )
    private String actualControllerId;
    @Column(
            name = "ACTUAL_CONTROLLER_ORG_CODE"
    )
    private String actualControllerOrgCode;
    @Column(
            name = "BP_NAME_SP_CLASS"
    )
    private String bpNameSpClass;
    @Column(
            name = "FIN_NOTE1"
    )
    private String finNote1;
    @Column(
            name = "FIN_NOTE"
    )
    private String finNote;
    @Column(
            name = "FIN_TURNOVER"
    )
    private String finTurnover;
    @Column(
            name = "BANK_SHORT_NAME"
    )
    private String bankShortName;

    private String actualCurrency;

    public String getActualCurrencyN() {
        return actualCurrencyN;
    }

    public void setActualCurrencyN(String actualCurrencyN) {
        this.actualCurrencyN = actualCurrencyN;
    }

    public String getRegistrationNumTypeN() {
        return registrationNumTypeN;
    }

    public void setRegistrationNumTypeN(String registrationNumTypeN) {
        this.registrationNumTypeN = registrationNumTypeN;
    }

    public String getRefV01N() {
        return refV01N;
    }

    public void setRefV01N(String refV01N) {
        this.refV01N = refV01N;
    }

    @Transient
    private String actualCurrencyN;
    @Transient
    private String registrationNumTypeN;
    @Transient
    private String refV01N;

    @Transient
    private String existenceStatusN;
    @Transient
    private String legalFormN;
    @Transient
    private String actualTypeN;
    @Transient
    private String description;
    private String shortName;


    private String existenceStatus;

    public String getActualCurrency() {
        return actualCurrency;
    }

    public void setActualCurrency(String actualCurrency) {
        this.actualCurrency = actualCurrency;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }



    public String getExistenceStatus() {
        return existenceStatus;
    }

    public void setExistenceStatus(String existenceStatus) {
        this.existenceStatus = existenceStatus;
    }



    public String getExpiryCertificate() {
        return expiryCertificate;
    }

    public void setExpiryCertificate(String expiryCertificate) {
        this.expiryCertificate = expiryCertificate;
    }


    private String expiryCertificate;
    private String authorityRuleString;
    @Column(
            name = "FIN_ORG_TYPE"
    )
    private String finOrgType;
    @Column(
            name = "START_ACTIVE_DATE"
    )
    private Date startActiveDate;
    @Column(
            name = "END_ACTIVE_DATE"
    )
    private Date endActiveDate;
    @Column(
            name = "ENTERPRISE_UNIFIED_CREDITCODE"
    )
    private String enterpriseUnifiedCreditcode;
    @Column(
            name = "BANK_CODE"
    )
    private String bankCode;

    private String groupCompanies;
    private String groupEnterpriseFlag;

    public String getAssetTypeN() {
        return assetTypeN;
    }

    public void setAssetTypeN(String assetTypeN) {
        this.assetTypeN = assetTypeN;
    }

    public String getGroupCompanies() {
        return groupCompanies;
    }

    public void setGroupCompanies(String groupCompanies) {
        this.groupCompanies = groupCompanies;
    }

    public String getGroupEnterpriseFlag() {
        return groupEnterpriseFlag;
    }

    public void setGroupEnterpriseFlag(String groupEnterpriseFlag) {
        this.groupEnterpriseFlag = groupEnterpriseFlag;
    }

    public String getGroupEnterpriseFlagN() {
        return groupEnterpriseFlagN;
    }

    public void setGroupEnterpriseFlagN(String groupEnterpriseFlagN) {
        this.groupEnterpriseFlagN = groupEnterpriseFlagN;
    }

    public String getBpFinancialTypeN() {
        return bpFinancialTypeN;
    }

    public void setBpFinancialTypeN(String bpFinancialTypeN) {
        this.bpFinancialTypeN = bpFinancialTypeN;
    }

    public String getEnterpriseNatureN() {
        return enterpriseNatureN;
    }

    public void setEnterpriseNatureN(String enterpriseNatureN) {
        this.enterpriseNatureN = enterpriseNatureN;
    }

    @Transient
    private String groupEnterpriseFlagN;
    @Transient
    private String refV05N;
    @Transient
    private String companyNatureN;
    @Transient
    private String bpNameSpClassN;
    @Transient
    private String actualControllerIdTypeN;
    @Transient
    private String currencyN;
    @Transient
    private String licenseTermsIfLongN;
    @Transient
    private String creditRatingN;
    @Transient
    private String marketLocationN;
    @Transient
    private String enterpriseScaleN;
    @Transient
    private String saleTypeN;
    @Transient
    private String refV06N;
    @Transient
    private String refV07N;
    @Transient
    private String nationalityN;
    @Transient
    private String invoiceKindN;
    @Transient
    private String taxpayerTypeN;
    @Transient
    private String ifToZxFlagN;
    @Transient
    private String bpCategoryN;
    @Transient
    private String idTypeN;

    public String getExistenceStatusN() {
        return existenceStatusN;
    }

    public void setExistenceStatusN(String existenceStatusN) {
        this.existenceStatusN = existenceStatusN;
    }

    public String getLegalFormN() {
        return legalFormN;
    }

    public void setLegalFormN(String legalFormN) {
        this.legalFormN = legalFormN;
    }

    public String getActualTypeN() {
        return actualTypeN;
    }

    public void setActualTypeN(String actualTypeN) {
        this.actualTypeN = actualTypeN;
    }

    @Transient
    private String bpTitleN;
    @Transient
    private String genderN;
    @Transient
    private String maritalStatusN;
    @Transient
    private String academicBackgroundN;
    @Transient
    private String academicBackgroundSpN;
    @Transient
    private String idTypeSpN;
    @Transient
    private String countrySpN;
    @Transient
    private String provinceSpN;
    @Transient
    private String citySpN;
    @Transient
    private String districtSpN;
    @Transient
    private String industryN;
    @Transient
    private String economicInduClassifyN;
    @Transient
    private Long recordId;

    public String getEconomicInduClassifyN() {
        return economicInduClassifyN;
    }

    public void setEconomicInduClassifyN(String economicInduClassifyN) {
        this.economicInduClassifyN = economicInduClassifyN;
    }

    @Transient
    private String userIdN;
    @Transient
    private String queryInfo;
    @Transient
    private String finOrgTypeN;

    private String legalRepresentative;
    private String actualNumber;
    private String controlRatio;
    private String idExpirationDate;
    private String actualExpirationDate;

    public String getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(String actualNumber) {
        this.actualNumber = actualNumber;
    }

    public String getControlRatio() {
        return controlRatio;
    }

    public void setControlRatio(String controlRatio) {
        this.controlRatio = controlRatio;
    }

    public String getIdExpirationDate() {
        return idExpirationDate;
    }

    public void setIdExpirationDate(String idExpirationDate) {
        this.idExpirationDate = idExpirationDate;
    }

    private String releComFlagN;

    @Transient
    private String releComFlag;

    @Transient
    private String bankIdN;

    private Long bankId;

    private String bpEngName;

    private Long paidInCapital;

    private String registerCapitalCur;

    private String registrationNumType;

    private String registerCertNum;

    private Date operatingPeriodEnd;

    private String groupCustomers;

    private String economicInduClassify;

    private String exclusiveTrade;

    private String customerNature;

    private String enterpriseNature;

    private String economicZone;

    private String listedCompany;

    private String organizationType;

    private String organizationTypeDetails;

    private String creditCustomer;

    private String customerNumber;

    private String bpFinancialType;
    @Transient
    private String bpFinancialTypeN;
    @Transient
    private String enterpriseNatureN;

    private String swift;

    private String localFinance;

    private String exitFinancing;

    private String bpCategroy;

    @Transient
    private String bpIdN;

    private String groupMembership;

    @Transient
    private String marketingReportId;

    @Transient
    private String createN;

    private String bpClassify;
    private String bpApproveStatus;

    @Transient
    private String bpClassifyN;

    @Transient
    private String categoryDesc;

    @Transient
    private String typeDesc;

    @Transient
    private String[] bpCategorys;

    private String virtualAccountFlag;

    private String regnotype;

    private String regno;

    /**
     * 协议到期日
     */
    private Date dealEndDate;

    //存续状态
    @Column(name = "SUBSISTING_STATUS")
    private String subsistingStatus;

    //存续状态名名称
    @Transient
    private String subsistingStatusN;


    //组织机构类型名称
    @Transient
    private String organizationTypeN;

    //业务范围
    @Column(name = "BUSINESS_SCOPE")
    private String businessScope;

    //注册资本币种
    @Column(name = "REGISTERED_CAPITAL_CURRENCY")
    private String registeredCapitalCurrency;

    //注册资本币种名称
    @Transient
    private String registeredCapitalCurrencyN;

    //实际控制人身份标识类型
    @Column(name = "ACTUAL_CTR_IDENTITY_TYPE")
    private String actualCtrIdentityType;

    //实际控制人身份标识类型名称
    @Transient
    private String actualCtrIdentityTypeN;

    //实际控制人身份号码
    @Column(name = "ACTUAL_CTR_IDENTITY_CODE")
    private String actualCtrIdentityCode;

    //上级机构类型
    @Column(name = "SUPERIOR_INST_TYPE")
    private String superiorInstType;

    //上级机构类型名称
    @Transient
    private String superiorInstTypeN;

    //上级机构名称
    @Column(name = "SUPERIOR_INST_NAME")
    private String superiorInstName;

    //上级机构身份标识类型
    @Column(name = "SUPERIOR_INST_MARK_TYPE")
    private String superiorInstMarkType;

    //上级机构身份标识类型名称
    @Transient
    private String superiorInstMarkTypeN;

    //上级机构标识号码
    @Column(name = "SUPERIOR_INST_MARK_CODE")
    private String superiorInstMarkCode;

    //配偶工作单位
    @Column(name = "SPOUSE_JOBS_UNIT")
    private String spouseJobsUnit;

    //学位
    @Column(name = "ACADEMIC_DEGREE")
    private String academicDegree;

    //学位名称
    @Transient
    private String academicDegreeN;

    //就业状况
    @Column(name = "EMPLOYMENT_STATUS")
    private String employmentStatus;

    //就业状况名称
    @Transient
    private String employmentStatusN;

    //职业
    @Column(name = "PROFESSION")
    private String profession;

    //职业名称
    @Transient
    private String professionN;

    //职称
    @Column(name = "JOB_TITLE")
    private String jobTitle;

    //职称名称
    @Transient
    private String jobTitleN;

    //工作单位起始年份
    @Column(name = "EMPLOYER_START_YEAR")
    private Date employerStartYear;

    //资产分级类型
    private String assetClassType;
    @Transient
    private String assetClassTypeN;

    //预审状态
    @Column(name = "pre_approval_status")
    private String preApprovalStatus;

    @Transient
    private String preApprovalStatusN;

    //预审有效期从
    @Column(name = "pre_valid_date_from")
    private Date preValidDateFrom;

    //预审有效期到
    @Column(name = "pre_valid_date_to")
    private Date preValidDateTo;

    //预审有效期（天）
    @Column(name = "pre_expiry_date")
    private Long preExpiryDate;

    //设备类型
    private String equipmentType;
    @Transient
    private String equipmentTypeN;

    //内部客户
    private String internalBp;
    @Transient
    private String internalBpN;

    //业务类型
    private String serviceType;
    @Transient
    private String serviceTypeN;

    @Transient
    private String rating;

    @Transient
    private Double ratingDiscount;

    @Transient
    private Date validDate;

    @Transient
    private String bpShareholderId;

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getExistFlag() {
        return existFlag;
    }

    public void setExistFlag(String existFlag) {
        this.existFlag = existFlag;
    }

    public String getRegnotype() {
        return regnotype;
    }

    public void setRegnotype(String regnotype) {
        this.regnotype = regnotype;
    }

    public String getVirtualAccountFlag() {
        return virtualAccountFlag;
    }

    public void setVirtualAccountFlag(String virtualAccountFlag) {
        this.virtualAccountFlag = virtualAccountFlag;
    }

    public String getBpShareholderId() {
        return bpShareholderId;
    }

    public void setBpShareholderId(String bpShareholderId) {
        this.bpShareholderId = bpShareholderId;
    }

    public Date getDealEndDate() {
        return dealEndDate;
    }

    public void setDealEndDate(Date dealEndDate) {
        this.dealEndDate = dealEndDate;
    }

    public String getSubsistingStatus() {
        return subsistingStatus;
    }

    public void setSubsistingStatus(String subsistingStatus) {
        this.subsistingStatus = subsistingStatus;
    }

    public String getSubsistingStatusN() {
        return subsistingStatusN;
    }

    public void setSubsistingStatusN(String subsistingStatusN) {
        this.subsistingStatusN = subsistingStatusN;
    }

    public String getOrganizationTypeN() {
        return organizationTypeN;
    }

    public void setOrganizationTypeN(String organizationTypeN) {
        this.organizationTypeN = organizationTypeN;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public String getRegisteredCapitalCurrency() {
        return registeredCapitalCurrency;
    }

    public void setRegisteredCapitalCurrency(String registeredCapitalCurrency) {
        this.registeredCapitalCurrency = registeredCapitalCurrency;
    }

    public String getRegisteredCapitalCurrencyN() {
        return registeredCapitalCurrencyN;
    }

    public void setRegisteredCapitalCurrencyN(String registeredCapitalCurrencyN) {
        this.registeredCapitalCurrencyN = registeredCapitalCurrencyN;
    }

    public String getActualCtrIdentityType() {
        return actualCtrIdentityType;
    }

    public void setActualCtrIdentityType(String actualCtrIdentityType) {
        this.actualCtrIdentityType = actualCtrIdentityType;
    }

    public String getActualCtrIdentityTypeN() {
        return actualCtrIdentityTypeN;
    }

    public void setActualCtrIdentityTypeN(String actualCtrIdentityTypeN) {
        this.actualCtrIdentityTypeN = actualCtrIdentityTypeN;
    }

    public String getActualCtrIdentityCode() {
        return actualCtrIdentityCode;
    }

    public void setActualCtrIdentityCode(String actualCtrIdentityCode) {
        this.actualCtrIdentityCode = actualCtrIdentityCode;
    }

    public String getSuperiorInstType() {
        return superiorInstType;
    }

    public void setSuperiorInstType(String superiorInstType) {
        this.superiorInstType = superiorInstType;
    }

    public String getSuperiorInstTypeN() {
        return superiorInstTypeN;
    }

    public void setSuperiorInstTypeN(String superiorInstTypeN) {
        this.superiorInstTypeN = superiorInstTypeN;
    }

    public String getSuperiorInstName() {
        return superiorInstName;
    }

    public void setSuperiorInstName(String superiorInstName) {
        this.superiorInstName = superiorInstName;
    }

    public String getSuperiorInstMarkType() {
        return superiorInstMarkType;
    }

    public void setSuperiorInstMarkType(String superiorInstMarkType) {
        this.superiorInstMarkType = superiorInstMarkType;
    }

    public String getSuperiorInstMarkTypeN() {
        return superiorInstMarkTypeN;
    }

    public void setSuperiorInstMarkTypeN(String superiorInstMarkTypeN) {
        this.superiorInstMarkTypeN = superiorInstMarkTypeN;
    }

    public String getSuperiorInstMarkCode() {
        return superiorInstMarkCode;
    }

    public void setSuperiorInstMarkCode(String superiorInstMarkCode) {
        this.superiorInstMarkCode = superiorInstMarkCode;
    }

    public String getSpouseJobsUnit() {
        return spouseJobsUnit;
    }

    public void setSpouseJobsUnit(String spouseJobsUnit) {
        this.spouseJobsUnit = spouseJobsUnit;
    }

    public String getAcademicDegree() {
        return academicDegree;
    }

    public void setAcademicDegree(String academicDegree) {
        this.academicDegree = academicDegree;
    }

    public String getAcademicDegreeN() {
        return academicDegreeN;
    }

    public void setAcademicDegreeN(String academicDegreeN) {
        this.academicDegreeN = academicDegreeN;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public String getEmploymentStatusN() {
        return employmentStatusN;
    }

    public void setEmploymentStatusN(String employmentStatusN) {
        this.employmentStatusN = employmentStatusN;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getProfessionN() {
        return professionN;
    }

    public void setProfessionN(String professionN) {
        this.professionN = professionN;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobTitleN() {
        return jobTitleN;
    }

    public void setJobTitleN(String jobTitleN) {
        this.jobTitleN = jobTitleN;
    }

    public Date getEmployerStartYear() {
        return employerStartYear;
    }

    public void setEmployerStartYear(Date employerStartYear) {
        this.employerStartYear = employerStartYear;
    }

    public String getAssetClassType() {
        return assetClassType;
    }

    public void setAssetClassType(String assetClassType) {
        this.assetClassType = assetClassType;
    }

    public String getAssetClassTypeN() {
        return assetClassTypeN;
    }

    public void setAssetClassTypeN(String assetClassTypeN) {
        this.assetClassTypeN = assetClassTypeN;
    }

    public String getPreApprovalStatus() {
        return preApprovalStatus;
    }

    public void setPreApprovalStatus(String preApprovalStatus) {
        this.preApprovalStatus = preApprovalStatus;
    }

    public String getPreApprovalStatusN() {
        return preApprovalStatusN;
    }

    public void setPreApprovalStatusN(String preApprovalStatusN) {
        this.preApprovalStatusN = preApprovalStatusN;
    }

    public Date getPreValidDateFrom() {
        return preValidDateFrom;
    }

    public void setPreValidDateFrom(Date preValidDateFrom) {
        this.preValidDateFrom = preValidDateFrom;
    }

    public Date getPreValidDateTo() {
        return preValidDateTo;
    }

    public void setPreValidDateTo(Date preValidDateTo) {
        this.preValidDateTo = preValidDateTo;
    }

    public Long getPreExpiryDate() {
        return preExpiryDate;
    }

    public void setPreExpiryDate(Long preExpiryDate) {
        this.preExpiryDate = preExpiryDate;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getEquipmentTypeN() {
        return equipmentTypeN;
    }

    public void setEquipmentTypeN(String equipmentTypeN) {
        this.equipmentTypeN = equipmentTypeN;
    }

    public String getInternalBp() {
        return internalBp;
    }

    public void setInternalBp(String internalBp) {
        this.internalBp = internalBp;
    }

    public String getInternalBpN() {
        return internalBpN;
    }

    public void setInternalBpN(String internalBpN) {
        this.internalBpN = internalBpN;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceTypeN() {
        return serviceTypeN;
    }

    public void setServiceTypeN(String serviceTypeN) {
        this.serviceTypeN = serviceTypeN;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Double getRatingDiscount() {
        return ratingDiscount;
    }

    public void setRatingDiscount(Double ratingDiscount) {
        this.ratingDiscount = ratingDiscount;
    }

    public Date getValidDate() {
        return validDate;
    }

    public void setValidDate(Date validDate) {
        this.validDate = validDate;
    }

    public String[] getBpCategorys() {
        return bpCategorys;
    }

    public void setBpCategorys(String[] bpCategorys) {
        this.bpCategorys = bpCategorys;
    }

    public String getBpClassify() {
        return bpClassify;
    }

    public void setBpClassify(String bpClassify) {
        this.bpClassify = bpClassify;
    }

    public String getBpClassifyN() {
        return bpClassifyN;
    }

    public void setBpClassifyN(String bpClassifyN) {
        this.bpClassifyN = bpClassifyN;
    }

    public HlsBpMaster() {
    }

    //融资经理
    private String financeManager;
    @Transient
    private String financeManagerN;
    //经办支行
    private String subBranch;

    public String getSubBranch() {
        return subBranch;
    }

    public void setSubBranch(String subBranch) {
        this.subBranch = subBranch;
    }

    public String getFinanceManagerN() {
        return financeManagerN;
    }

    public void setFinanceManagerN(String financeManagerN) {
        this.financeManagerN = financeManagerN;
    }

    public String getFinanceManager() {
        return financeManager;
    }

    public void setFinanceManager(String financeManager) {
        this.financeManager = financeManager;
    }

    public String getCreateN() {
        return createN;
    }

    public void setCreateN(String createN) {
        this.createN = createN;
    }

    public String getUnitId() {
        return this.unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getQueryInfo() {
        return this.queryInfo;
    }

    public void setQueryInfo(String queryInfo) {
        this.queryInfo = queryInfo;
    }

    public Long getRecordId() {
        return this.recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getIndustryN() {
        return this.industryN;
    }

    public void setIndustryN(String industryN) {
        this.industryN = industryN;
    }

    public String getBpCodeFromN() {
        return this.bpCodeFromN;
    }

    public void setBpCodeFromN(String bpCodeFromN) {
        this.bpCodeFromN = bpCodeFromN;
    }

    public String getBpCodeToN() {
        return this.bpCodeToN;
    }

    public void setBpCodeToN(String bpCodeToN) {
        this.bpCodeToN = bpCodeToN;
    }

    public String getActualController() {
        return this.actualController;
    }

    public void setActualController(String actualController) {
        this.actualController = actualController;
    }

    public String getCountrySpN() {
        return this.countrySpN;
    }

    public void setCountrySpN(String countrySpN) {
        this.countrySpN = countrySpN;
    }

    public String getProvinceSpN() {
        return this.provinceSpN;
    }

    public void setProvinceSpN(String provinceSpN) {
        this.provinceSpN = provinceSpN;
    }

    public String getCitySpN() {
        return this.citySpN;
    }

    public void setCitySpN(String citySpN) {
        this.citySpN = citySpN;
    }

    public String getDistrictSpN() {
        return this.districtSpN;
    }

    public void setDistrictSpN(String districtSpN) {
        this.districtSpN = districtSpN;
    }

    public String getAcademicBackgroundSpN() {
        return this.academicBackgroundSpN;
    }

    public void setAcademicBackgroundSpN(String academicBackgroundSpN) {
        this.academicBackgroundSpN = academicBackgroundSpN;
    }

    public String getIdTypeSpN() {
        return this.idTypeSpN;
    }

    public void setIdTypeSpN(String idTypeSpN) {
        this.idTypeSpN = idTypeSpN;
    }

    public String getAcademicBackgroundN() {
        return this.academicBackgroundN;
    }

    public void setAcademicBackgroundN(String academicBackgroundN) {
        this.academicBackgroundN = academicBackgroundN;
    }

    public String getMaritalStatusN() {
        return this.maritalStatusN;
    }

    public void setMaritalStatusN(String maritalStatusN) {
        this.maritalStatusN = maritalStatusN;
    }

    public String getGenderN() {
        return this.genderN;
    }

    public void setGenderN(String genderN) {
        this.genderN = genderN;
    }

    public String getBpTitleN() {
        return this.bpTitleN;
    }

    public void setBpTitleN(String bpTitleN) {
        this.bpTitleN = bpTitleN;
    }

    public String getIdTypeN() {
        return this.idTypeN;
    }

    public void setIdTypeN(String idTypeN) {
        this.idTypeN = idTypeN;
    }

    public String getIfToZxFlagN() {
        return this.ifToZxFlagN;
    }

    public void setIfToZxFlagN(String ifToZxFlagN) {
        this.ifToZxFlagN = ifToZxFlagN;
    }

    public String getBpCategoryN() {
        return this.bpCategoryN;
    }

    public void setBpCategoryN(String bpCategoryN) {
        this.bpCategoryN = bpCategoryN;
    }

    public String getAuthorityRuleString() {
        return this.authorityRuleString;
    }

    public void setAuthorityRuleString(String authorityRuleString) {
        this.authorityRuleString = authorityRuleString;
    }

    public String getInvoiceKindN() {
        return this.invoiceKindN;
    }

    public void setInvoiceKindN(String invoiceKindN) {
        this.invoiceKindN = invoiceKindN;
    }

    public String getTaxpayerTypeN() {
        return this.taxpayerTypeN;
    }

    public void setTaxpayerTypeN(String taxpayerTypeN) {
        this.taxpayerTypeN = taxpayerTypeN;
    }

    public String getNationalityN() {
        return this.nationalityN;
    }

    public void setNationalityN(String nationalityN) {
        this.nationalityN = nationalityN;
    }

    public String getRefV05N() {
        return this.refV05N;
    }

    public void setRefV05N(String refV05N) {
        this.refV05N = refV05N;
    }

    public String getCompanyNatureN() {
        return this.companyNatureN;
    }

    public void setCompanyNatureN(String companyNatureN) {
        this.companyNatureN = companyNatureN;
    }

    public String getBpNameSpClassN() {
        return this.bpNameSpClassN;
    }

    public void setBpNameSpClassN(String bpNameSpClassN) {
        this.bpNameSpClassN = bpNameSpClassN;
    }

    public String getActualControllerIdTypeN() {
        return this.actualControllerIdTypeN;
    }

    public void setActualControllerIdTypeN(String actualControllerIdTypeN) {
        this.actualControllerIdTypeN = actualControllerIdTypeN;
    }

    public String getCurrencyN() {
        return this.currencyN;
    }

    public void setCurrencyN(String currencyN) {
        this.currencyN = currencyN;
    }

    public String getLicenseTermsIfLongN() {
        return this.licenseTermsIfLongN;
    }

    public void setLicenseTermsIfLongN(String licenseTermsIfLongN) {
        this.licenseTermsIfLongN = licenseTermsIfLongN;
    }

    public String getCreditRatingN() {
        return this.creditRatingN;
    }

    public void setCreditRatingN(String creditRatingN) {
        this.creditRatingN = creditRatingN;
    }

    public String getMarketLocationN() {
        return this.marketLocationN;
    }

    public void setMarketLocationN(String marketLocationN) {
        this.marketLocationN = marketLocationN;
    }

    public String getEnterpriseScaleN() {
        return this.enterpriseScaleN;
    }

    public void setEnterpriseScaleN(String enterpriseScaleN) {
        this.enterpriseScaleN = enterpriseScaleN;
    }

    public String getSaleTypeN() {
        return this.saleTypeN;
    }

    public void setSaleTypeN(String saleTypeN) {
        this.saleTypeN = saleTypeN;
    }

    public String getRefV06N() {
        return this.refV06N;
    }

    public void setRefV06N(String refV06N) {
        this.refV06N = refV06N;
    }

    public String getRefV07N() {
        return this.refV07N;
    }

    public void setRefV07N(String refV07N) {
        this.refV07N = refV07N;
    }

    public String getBpClassN() {
        return this.bpClassN;
    }

    public void setBpClassN(String bpClassN) {
        this.bpClassN = bpClassN;
    }

    public String getBankShortName() {
        return this.bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getOwnerUserIdN() {
        return this.ownerUserIdN;
    }

    public void setOwnerUserIdN(String ownerUserIdN) {
        this.ownerUserIdN = ownerUserIdN;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectNumber() {
        return this.projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getContractId() {
        return this.contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractNumber() {
        return this.contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractName() {
        return this.contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public Long getOwnerUserId() {
        return this.ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getFinOrgType() {
        return this.finOrgType;
    }

    public void setFinOrgType(String finOrgType) {
        this.finOrgType = finOrgType;
    }

    public String getBpCode() {
        return this.bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getBpName() {
        return this.bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getExtraNam() {
        return this.extraNam;
    }

    public void setExtraNam(String extraNam) {
        this.extraNam = extraNam;
    }

    public String getBpClass() {
        return this.bpClass;
    }

    public void setBpClass(String bpClass) {
        this.bpClass = bpClass;
    }

    public String getBpCategory() {
        return this.bpCategory;
    }

    public void setBpCategory(String bpCategory) {
        this.bpCategory = bpCategory;
    }

    public String getBpType() {
        return this.bpType;
    }

    public String getBpTypeN() {
        return this.bpTypeN;
    }

    public void setBpTypeN(String bpTypeN) {
        this.bpTypeN = bpTypeN;
    }

    public void setBpType(String bpType) {
        this.bpType = bpType;
    }

    public String getBpTitle() {
        return this.bpTitle;
    }

    public void setBpTitle(String bpTitle) {
        this.bpTitle = bpTitle;
    }

    public String getSearchTerm1() {
        return this.searchTerm1;
    }

    public void setSearchTerm1(String searchTerm1) {
        this.searchTerm1 = searchTerm1;
    }

    public Long getSearchTerm2() {
        return this.searchTerm2;
    }

    public void setSearchTerm2(Long searchTerm2) {
        this.searchTerm2 = searchTerm2;
    }

    public String getExternalBpCode() {
        return this.externalBpCode;
    }

    public void setExternalBpCode(String externalBpCode) {
        this.externalBpCode = externalBpCode;
    }

    public Long getAddressId() {
        return this.addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return this.nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPlaceOfBirth() {
        return this.placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getNameAtBirth() {
        return this.nameAtBirth;
    }

    public void setNameAtBirth(String nameAtBirth) {
        this.nameAtBirth = nameAtBirth;
    }

    public String getMaritalStatus() {
        return this.maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Long getNumberOfChildren() {
        return this.numberOfChildren;
    }

    public void setNumberOfChildren(Long numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public String getAcademicBackground() {
        return this.academicBackground;
    }

    public void setAcademicBackground(String academicBackground) {
        this.academicBackground = academicBackground;
    }

    public Long getAge() {
        return this.age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getIdType() {
        return this.idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdCardNo() {
        return this.idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public Long getAnnualIncome() {
        return this.annualIncome;
    }

    public void setAnnualIncome(Long annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getCapitalOfFamily() {
        return this.capitalOfFamily;
    }

    public void setCapitalOfFamily(Long capitalOfFamily) {
        this.capitalOfFamily = capitalOfFamily;
    }

    public Long getLiabilityOfFamily() {
        return this.liabilityOfFamily;
    }

    public void setLiabilityOfFamily(Long liabilityOfFamily) {
        this.liabilityOfFamily = liabilityOfFamily;
    }

    public String getLegalForm() {
        return this.legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    public String getIndustry() {
        return this.industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getBusinessLicenseNum() {
        return this.businessLicenseNum;
    }

    public void setBusinessLicenseNum(String businessLicenseNum) {
        this.businessLicenseNum = businessLicenseNum;
    }

    public String getCorporateCode() {
        return this.corporateCode;
    }

    public void setCorporateCode(String corporateCode) {
        this.corporateCode = corporateCode;
    }

    public String getOrganizationCode() {
        return this.organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getTaxRegistryNum() {
        return this.taxRegistryNum;
    }

    public void setTaxRegistryNum(String taxRegistryNum) {
        this.taxRegistryNum = taxRegistryNum;
    }

    public String getRegisteredPlace() {
        return this.registeredPlace;
    }

    public void setRegisteredPlace(String registeredPlace) {
        this.registeredPlace = registeredPlace;
    }

    public Date getFoundedDate() {
        return this.foundedDate;
    }

    public void setFoundedDate(Date foundedDate) {
        this.foundedDate = foundedDate;
    }

    public Long getRegisteredCapital() {
        return this.registeredCapital;
    }

    public void setRegisteredCapital(Long registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    public String getBalanceSheetCurrency() {
        return this.balanceSheetCurrency;
    }

    public void setBalanceSheetCurrency(String balanceSheetCurrency) {
        this.balanceSheetCurrency = balanceSheetCurrency;
    }

    public String getTaxpayerType() {
        return this.taxpayerType;
    }

    public void setTaxpayerType(String taxpayerType) {
        this.taxpayerType = taxpayerType;
    }

    public String getInvoiceTitle() {
        return this.invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getInvoiceBpAddressPhoneNum() {
        return this.invoiceBpAddressPhoneNum;
    }

    public void setInvoiceBpAddressPhoneNum(String invoiceBpAddressPhoneNum) {
        this.invoiceBpAddressPhoneNum = invoiceBpAddressPhoneNum;
    }

    public String getInvoiceBpBankAccount() {
        return this.invoiceBpBankAccount;
    }

    public void setInvoiceBpBankAccount(String invoiceBpBankAccount) {
        this.invoiceBpBankAccount = invoiceBpBankAccount;
    }

    public String getLoanCardNum() {
        return this.loanCardNum;
    }

    public void setLoanCardNum(String loanCardNum) {
        this.loanCardNum = loanCardNum;
    }

    public String getPaidUpCapital() {
        return this.paidUpCapital;
    }

    public void setPaidUpCapital(String paidUpCapital) {
        this.paidUpCapital = paidUpCapital;
    }

    public String getCompanyNature() {
        return this.companyNature;
    }

    public void setCompanyNature(String companyNature) {
        this.companyNature = companyNature;
    }

    public String getPrimaryBusiness() {
        return this.primaryBusiness;
    }

    public void setPrimaryBusiness(String primaryBusiness) {
        this.primaryBusiness = primaryBusiness;
    }

    public String getMainProducts() {
        return this.mainProducts;
    }

    public void setMainProducts(String mainProducts) {
        this.mainProducts = mainProducts;
    }

    public String getBpNameSp() {
        return this.bpNameSp;
    }

    public void setBpNameSp(String bpNameSp) {
        this.bpNameSp = bpNameSp;
    }

    public String getGenderSp() {
        return this.genderSp;
    }

    public void setGenderSp(String genderSp) {
        this.genderSp = genderSp;
    }

    public Date getDateOfBirthSp() {
        return this.dateOfBirthSp;
    }

    public void setDateOfBirthSp(Date dateOfBirthSp) {
        this.dateOfBirthSp = dateOfBirthSp;
    }

    public String getAcademicBackgroundSp() {
        return this.academicBackgroundSp;
    }

    public void setAcademicBackgroundSp(String academicBackgroundSp) {
        this.academicBackgroundSp = academicBackgroundSp;
    }

    public String getIdTypeSp() {
        return this.idTypeSp;
    }

    public void setIdTypeSp(String idTypeSp) {
        this.idTypeSp = idTypeSp;
    }

    public String getIdCardNoSp() {
        return this.idCardNoSp;
    }

    public void setIdCardNoSp(String idCardNoSp) {
        this.idCardNoSp = idCardNoSp;
    }

    public String getCountrySp() {
        return this.countrySp;
    }

    public void setCountrySp(String countrySp) {
        this.countrySp = countrySp;
    }

    public String getProvinceSp() {
        return this.provinceSp;
    }

    public void setProvinceSp(String provinceSp) {
        this.provinceSp = provinceSp;
    }

    public String getCitySp() {
        return this.citySp;
    }

    public void setCitySp(String citySp) {
        this.citySp = citySp;
    }

    public String getDistrictSp() {
        return this.districtSp;
    }

    public void setDistrictSp(String districtSp) {
        this.districtSp = districtSp;
    }

    public String getAddressSp() {
        return this.addressSp;
    }

    public void setAddressSp(String addressSp) {
        this.addressSp = addressSp;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }



    public Long getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }


    public Date getCreationDate() {
        return this.creationDate;
    }

    public String getUnitIdN() {
        return unitIdN;
    }

    public void setUnitIdN(String unitIdN) {
        this.unitIdN = unitIdN;
    }

    public String getCreationDateN() {
        return creationDateN;
    }

    public void setCreationDateN(String creationDateN) {
        this.creationDateN = creationDateN;
    }

    public String getBpApproveStatusN() {
        return bpApproveStatusN;
    }

    public void setBpApproveStatusN(String bpApproveStatusN) {
        this.bpApproveStatusN = bpApproveStatusN;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public Date getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdatetimeDate() {
        return this.lastUpdatetimeDate;
    }

    public void setLastUpdatetimeDate(Date lastUpdatetimeDate) {
        this.lastUpdatetimeDate = lastUpdatetimeDate;
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

    public String getRefV06() {
        return this.refV06;
    }

    public void setRefV06(String refV06) {
        this.refV06 = refV06;
    }

    public String getRefV07() {
        return this.refV07;
    }

    public void setRefV07(String refV07) {
        this.refV07 = refV07;
    }

    public String getRefV08() {
        return this.refV08;
    }

    public void setRefV08(String refV08) {
        this.refV08 = refV08;
    }

    public String getRefV09() {
        return this.refV09;
    }

    public void setRefV09(String refV09) {
        this.refV09 = refV09;
    }

    public String getRefV10() {
        return this.refV10;
    }

    public void setRefV10(String refV10) {
        this.refV10 = refV10;
    }

    public String getRefV11() {
        return this.refV11;
    }

    public void setRefV11(String refV11) {
        this.refV11 = refV11;
    }

    public String getRefV12() {
        return this.refV12;
    }

    public void setRefV12(String refV12) {
        this.refV12 = refV12;
    }

    public String getRefV13() {
        return this.refV13;
    }

    public void setRefV13(String refV13) {
        this.refV13 = refV13;
    }

    public String getRefV14() {
        return this.refV14;
    }

    public void setRefV14(String refV14) {
        this.refV14 = refV14;
    }

    public String getRefV15() {
        return this.refV15;
    }

    public void setRefV15(String refV15) {
        this.refV15 = refV15;
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

    public Long getRefN06() {
        return this.refN06;
    }

    public void setRefN06(Long refN06) {
        this.refN06 = refN06;
    }

    public Long getRefN07() {
        return this.refN07;
    }

    public void setRefN07(Long refN07) {
        this.refN07 = refN07;
    }

    public Long getRefN08() {
        return this.refN08;
    }

    public void setRefN08(Long refN08) {
        this.refN08 = refN08;
    }

    public Long getRefN09() {
        return this.refN09;
    }

    public void setRefN09(Long refN09) {
        this.refN09 = refN09;
    }

    public Long getRefN10() {
        return this.refN10;
    }

    public void setRefN10(Long refN10) {
        this.refN10 = refN10;
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

    public String getEnterpriseScale() {
        return this.enterpriseScale;
    }

    public void setEnterpriseScale(String enterpriseScale) {
        this.enterpriseScale = enterpriseScale;
    }

    public String getLegalPerson() {
        return this.legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    public Long getSpousePhone() {
        return this.spousePhone;
    }

    public void setSpousePhone(Long spousePhone) {
        this.spousePhone = spousePhone;
    }

    public Date getLicenseTerms() {
        return this.licenseTerms;
    }

    public void setLicenseTerms(Date licenseTerms) {
        this.licenseTerms = licenseTerms;
    }

    public Long getPorportionOfGuarantee() {
        return this.porportionOfGuarantee;
    }

    public void setPorportionOfGuarantee(Long porportionOfGuarantee) {
        this.porportionOfGuarantee = porportionOfGuarantee;
    }

    public String getPeriodInJob() {
        return this.periodInJob;
    }

    public void setPeriodInJob(String periodInJob) {
        this.periodInJob = periodInJob;
    }

    public Long getNetMonthlyIncome() {
        return this.netMonthlyIncome;
    }

    public void setNetMonthlyIncome(Long netMonthlyIncome) {
        this.netMonthlyIncome = netMonthlyIncome;
    }

    public String getLeaderFlag() {
        return this.leaderFlag;
    }

    public void setLeaderFlag(String leaderFlag) {
        this.leaderFlag = leaderFlag;
    }

    public String getMortgageFlag() {
        return this.mortgageFlag;
    }

    public void setMortgageFlag(String mortgageFlag) {
        this.mortgageFlag = mortgageFlag;
    }

    public String getLocalPersonFlag() {
        return this.localPersonFlag;
    }

    public void setLocalPersonFlag(String localPersonFlag) {
        this.localPersonFlag = localPersonFlag;
    }

    public String getHighMentalFlag() {
        return this.highMentalFlag;
    }

    public void setHighMentalFlag(String highMentalFlag) {
        this.highMentalFlag = highMentalFlag;
    }

    public String getHasHouseFlag() {
        return this.hasHouseFlag;
    }

    public void setHasHouseFlag(String hasHouseFlag) {
        this.hasHouseFlag = hasHouseFlag;
    }

    public String getHasCarFlag() {
        return this.hasCarFlag;
    }

    public void setHasCarFlag(String hasCarFlag) {
        this.hasCarFlag = hasCarFlag;
    }

    public String getCommunityLeaderFlag() {
        return this.communityLeaderFlag;
    }

    public void setCommunityLeaderFlag(String communityLeaderFlag) {
        this.communityLeaderFlag = communityLeaderFlag;
    }

    public String getBankMatchFlag() {
        return this.bankMatchFlag;
    }

    public void setBankMatchFlag(String bankMatchFlag) {
        this.bankMatchFlag = bankMatchFlag;
    }

    public Long getHousePropertyValue() {
        return this.housePropertyValue;
    }

    public void setHousePropertyValue(Long housePropertyValue) {
        this.housePropertyValue = housePropertyValue;
    }

    public Long getHouseLoanBalance() {
        return this.houseLoanBalance;
    }

    public void setHouseLoanBalance(Long houseLoanBalance) {
        this.houseLoanBalance = houseLoanBalance;
    }

    public String getDepositCertificate() {
        return this.depositCertificate;
    }

    public void setDepositCertificate(String depositCertificate) {
        this.depositCertificate = depositCertificate;
    }

    public String getCreditCardLimit() {
        return this.creditCardLimit;
    }

    public void setCreditCardLimit(String creditCardLimit) {
        this.creditCardLimit = creditCardLimit;
    }

    public String getAddressOnId() {
        return this.addressOnId;
    }

    public void setAddressOnId(String addressOnId) {
        this.addressOnId = addressOnId;
    }

    public String getAddressOnResidentBooklit() {
        return this.addressOnResidentBooklit;
    }

    public void setAddressOnResidentBooklit(String addressOnResidentBooklit) {
        this.addressOnResidentBooklit = addressOnResidentBooklit;
    }

    public String getLivingAddress() {
        return this.livingAddress;
    }

    public void setLivingAddress(String livingAddress) {
        this.livingAddress = livingAddress;
    }

    public String getWorkingPlace() {
        return this.workingPlace;
    }

    public void setWorkingPlace(String workingPlace) {
        this.workingPlace = workingPlace;
    }

    public String getWorkingDuration() {
        return this.workingDuration;
    }

    public void setWorkingDuration(String workingDuration) {
        this.workingDuration = workingDuration;
    }

    public String getWorkingAddress() {
        return this.workingAddress;
    }

    public void setWorkingAddress(String workingAddress) {
        this.workingAddress = workingAddress;
    }

    public String getOperationYear() {
        return this.operationYear;
    }

    public void setOperationYear(String operationYear) {
        this.operationYear = operationYear;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCellPhone() {
        return this.cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getPhoneExtra() {
        return this.phoneExtra;
    }

    public void setPhoneExtra(String phoneExtra) {
        this.phoneExtra = phoneExtra;
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

    public String getFaxNumber() {
        return this.faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCreditFlag() {
        return this.creditFlag;
    }

    public void setCreditFlag(String creditFlag) {
        this.creditFlag = creditFlag;
    }

    public Long getCreditAmount() {
        return this.creditAmount;
    }

    public void setCreditAmount(Long creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Long getCreditAlt() {
        return this.creditAlt;
    }

    public void setCreditAlt(Long creditAlt) {
        this.creditAlt = creditAlt;
    }

    public Long getCreditForbid() {
        return this.creditForbid;
    }

    public void setCreditForbid(Long creditForbid) {
        this.creditForbid = creditForbid;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNcStatus() {
        return this.ncStatus;
    }

    public void setNcStatus(String ncStatus) {
        this.ncStatus = ncStatus;
    }

    public String getCellPhone2() {
        return this.cellPhone2;
    }

    public void setCellPhone2(String cellPhone2) {
        this.cellPhone2 = cellPhone2;
    }

    public Long getEmployeeAmount() {
        return this.employeeAmount;
    }

    public void setEmployeeAmount(Long employeeAmount) {
        this.employeeAmount = employeeAmount;
    }

    public String getBillingStatus() {
        return this.billingStatus;
    }

    public void setBillingStatus(String billingStatus) {
        this.billingStatus = billingStatus;
    }

    public Long getFinNetCashInflow() {
        return this.finNetCashInflow;
    }

    public void setFinNetCashInflow(Long finNetCashInflow) {
        this.finNetCashInflow = finNetCashInflow;
    }

    public Long getFinMonthlyPayment() {
        return this.finMonthlyPayment;
    }

    public void setFinMonthlyPayment(Long finMonthlyPayment) {
        this.finMonthlyPayment = finMonthlyPayment;
    }

    public Long getFinMonths() {
        return this.finMonths;
    }

    public void setFinMonths(Long finMonths) {
        this.finMonths = finMonths;
    }

    public String getFinLiquidityRatio() {
        return this.finLiquidityRatio;
    }

    public void setFinLiquidityRatio(String finLiquidityRatio) {
        this.finLiquidityRatio = finLiquidityRatio;
    }

    public String getFinLeverage() {
        return this.finLeverage;
    }

    public void setFinLeverage(String finLeverage) {
        this.finLeverage = finLeverage;
    }

    public String getFinData() {
        return this.finData;
    }

    public void setFinData(String finData) {
        this.finData = finData;
    }

    public String getFinEvaluation() {
        return this.finEvaluation;
    }

    public void setFinEvaluation(String finEvaluation) {
        this.finEvaluation = finEvaluation;
    }

    public Long getCddListId() {
        return this.cddListId;
    }

    public void setCddListId(Long cddListId) {
        this.cddListId = cddListId;
    }

    public String getPbNumber() {
        return this.pbNumber;
    }

    public void setPbNumber(String pbNumber) {
        this.pbNumber = pbNumber;
    }

    public String getRepoNumber() {
        return this.repoNumber;
    }

    public void setRepoNumber(String repoNumber) {
        this.repoNumber = repoNumber;
    }

    public String getBlackFlag() {
        return this.blackFlag;
    }

    public void setBlackFlag(String blackFlag) {
        this.blackFlag = blackFlag;
    }

    public String getLockFlag() {
        return this.lockFlag;
    }

    public void setLockFlag(String lockFlag) {
        this.lockFlag = lockFlag;
    }

    public String getOldFlag() {
        return this.oldFlag;
    }

    public void setOldFlag(String oldFlag) {
        this.oldFlag = oldFlag;
    }

    public Date getLimitFrom() {
        return this.limitFrom;
    }

    public void setLimitFrom(Date limitFrom) {
        this.limitFrom = limitFrom;
    }

    public Date getLimitTo() {
        return this.limitTo;
    }

    public void setLimitTo(Date limitTo) {
        this.limitTo = limitTo;
    }

    public String getLawBpFlag() {
        return this.lawBpFlag;
    }

    public void setLawBpFlag(String lawBpFlag) {
        this.lawBpFlag = lawBpFlag;
    }

    public String getFinTurnover1() {
        return this.finTurnover1;
    }

    public void setFinTurnover1(String finTurnover1) {
        this.finTurnover1 = finTurnover1;
    }

    public String getIfToZxFlag() {
        return this.ifToZxFlag;
    }

    public void setIfToZxFlag(String ifToZxFlag) {
        this.ifToZxFlag = ifToZxFlag;
    }

    public Date getZxLastUpdateDate() {
        return this.zxLastUpdateDate;
    }

    public void setZxLastUpdateDate(Date zxLastUpdateDate) {
        this.zxLastUpdateDate = zxLastUpdateDate;
    }

    public Long getZxLastUpdatedBy() {
        return this.zxLastUpdatedBy;
    }

    public void setZxLastUpdatedBy(Long zxLastUpdatedBy) {
        this.zxLastUpdatedBy = zxLastUpdatedBy;
    }

    public Long getCreditAmountUsedFix() {
        return this.creditAmountUsedFix;
    }

    public void setCreditAmountUsedFix(Long creditAmountUsedFix) {
        this.creditAmountUsedFix = creditAmountUsedFix;
    }

    public Long getCreditAmountUsedCycle() {
        return this.creditAmountUsedCycle;
    }

    public void setCreditAmountUsedCycle(Long creditAmountUsedCycle) {
        this.creditAmountUsedCycle = creditAmountUsedCycle;
    }

    public Long getCreditAmountFrozenFix() {
        return this.creditAmountFrozenFix;
    }

    public void setCreditAmountFrozenFix(Long creditAmountFrozenFix) {
        this.creditAmountFrozenFix = creditAmountFrozenFix;
    }

    public Long getCreditAmountFrozenCycle() {
        return this.creditAmountFrozenCycle;
    }

    public void setCreditAmountFrozenCycle(Long creditAmountFrozenCycle) {
        this.creditAmountFrozenCycle = creditAmountFrozenCycle;
    }

    public Long getPollingTimes() {
        return this.pollingTimes;
    }

    public void setPollingTimes(Long pollingTimes) {
        this.pollingTimes = pollingTimes;
    }

    public String getSaleType() {
        return this.saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public String getAssetType() {
        return this.assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getArea() {
        return this.area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getListedCompanyFlag() {
        return this.listedCompanyFlag;
    }

    public void setListedCompanyFlag(String listedCompanyFlag) {
        this.listedCompanyFlag = listedCompanyFlag;
    }

    public String getMarketLocation() {
        return this.marketLocation;
    }

    public void setMarketLocation(String marketLocation) {
        this.marketLocation = marketLocation;
    }

    public String getCreditRating() {
        return this.creditRating;
    }

    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating;
    }

    public Date getRatingDate() {
        return this.ratingDate;
    }

    public void setRatingDate(Date ratingDate) {
        this.ratingDate = ratingDate;
    }

    public String getInvoiceKind() {
        return this.invoiceKind;
    }

    public void setInvoiceKind(String invoiceKind) {
        this.invoiceKind = invoiceKind;
    }

    public String getLicenseTermsIfLong() {
        return this.licenseTermsIfLong;
    }

    public void setLicenseTermsIfLong(String licenseTermsIfLong) {
        this.licenseTermsIfLong = licenseTermsIfLong;
    }

    public String getListedSubject() {
        return this.listedSubject;
    }

    public void setListedSubject(String listedSubject) {
        this.listedSubject = listedSubject;
    }

    public String getListedSubjectOrgCode() {
        return this.listedSubjectOrgCode;
    }

    public void setListedSubjectOrgCode(String listedSubjectOrgCode) {
        this.listedSubjectOrgCode = listedSubjectOrgCode;
    }

    public String getActualControllerIdType() {
        return this.actualControllerIdType;
    }

    public void setActualControllerIdType(String actualControllerIdType) {
        this.actualControllerIdType = actualControllerIdType;
    }

    public String getActualControllerId() {
        return this.actualControllerId;
    }

    public void setActualControllerId(String actualControllerId) {
        this.actualControllerId = actualControllerId;
    }

    public String getActualControllerOrgCode() {
        return this.actualControllerOrgCode;
    }

    public void setActualControllerOrgCode(String actualControllerOrgCode) {
        this.actualControllerOrgCode = actualControllerOrgCode;
    }

    public String getBpNameSpClass() {
        return this.bpNameSpClass;
    }

    public void setBpNameSpClass(String bpNameSpClass) {
        this.bpNameSpClass = bpNameSpClass;
    }

    public String getFinNote1() {
        return this.finNote1;
    }

    public void setFinNote1(String finNote1) {
        this.finNote1 = finNote1;
    }

    public String getFinNote() {
        return this.finNote;
    }

    public void setFinNote(String finNote) {
        this.finNote = finNote;
    }

    public String getFinTurnover() {
        return this.finTurnover;
    }

    public void setFinTurnover(String finTurnover) {
        this.finTurnover = finTurnover;
    }

    public String getUserIdN() {
        return this.userIdN;
    }

    public void setUserIdN(String userIdN) {
        this.userIdN = userIdN;
    }

    public Date getStartActiveDate() {
        return this.startActiveDate;
    }

    public void setStartActiveDate(Date startActiveDate) {
        this.startActiveDate = startActiveDate;
    }

    public Date getEndActiveDate() {
        return this.endActiveDate;
    }

    public void setEndActiveDate(Date endActiveDate) {
        this.endActiveDate = endActiveDate;
    }

    public String getEnterpriseUnifiedCreditcode() {
        return this.enterpriseUnifiedCreditcode;
    }

    public void setEnterpriseUnifiedCreditcode(String enterpriseUnifiedCreditcode) {
        this.enterpriseUnifiedCreditcode = enterpriseUnifiedCreditcode;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getFinOrgTypeN() {
        return this.finOrgTypeN;
    }

    public void setFinOrgTypeN(String finOrgTypeN) {
        this.finOrgTypeN = finOrgTypeN;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getReleComFlagN() {
        return releComFlagN;
    }

    public void setReleComFlagN(String releComFlagN) {
        this.releComFlagN = releComFlagN;
    }

    public String getReleComFlag() {
        return releComFlag;
    }

    public void setReleComFlag(String releComFlag) {
        this.releComFlag = releComFlag;
    }

    public String getBankIdN() {
        return bankIdN;
    }

    public void setBankIdN(String bankIdN) {
        this.bankIdN = bankIdN;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBpEngName() {
        return bpEngName;
    }

    public void setBpEngName(String bpEngName) {
        this.bpEngName = bpEngName;
    }

    public Long getPaidInCapital() {
        return paidInCapital;
    }

    public void setPaidInCapital(Long paidInCapital) {
        this.paidInCapital = paidInCapital;
    }

    public String getRegisterCapitalCur() {
        return registerCapitalCur;
    }

    public void setRegisterCapitalCur(String registerCapitalCur) {
        this.registerCapitalCur = registerCapitalCur;
    }

    public String getRegistrationNumType() {
        return registrationNumType;
    }

    public void setRegistrationNumType(String registrationNumType) {
        this.registrationNumType = registrationNumType;
    }

    public String getRegisterCertNum() {
        return registerCertNum;
    }

    public void setRegisterCertNum(String registerCertNum) {
        this.registerCertNum = registerCertNum;
    }

    public Date getOperatingPeriodEnd() {
        return operatingPeriodEnd;
    }

    public void setOperatingPeriodEnd(Date operatingPeriodEnd) {
        this.operatingPeriodEnd = operatingPeriodEnd;
    }

    public String getGroupCustomers() {
        return groupCustomers;
    }

    public void setGroupCustomers(String groupCustomers) {
        this.groupCustomers = groupCustomers;
    }

    public String getEconomicInduClassify() {
        return economicInduClassify;
    }

    public void setEconomicInduClassify(String economicInduClassify) {
        this.economicInduClassify = economicInduClassify;
    }

    public String getExclusiveTrade() {
        return exclusiveTrade;
    }

    public void setExclusiveTrade(String exclusiveTrade) {
        this.exclusiveTrade = exclusiveTrade;
    }

    public String getCustomerNature() {
        return customerNature;
    }

    public void setCustomerNature(String customerNature) {
        this.customerNature = customerNature;
    }

    public String getEnterpriseNature() {
        return enterpriseNature;
    }

    public void setEnterpriseNature(String enterpriseNature) {
        this.enterpriseNature = enterpriseNature;
    }

    public String getEconomicZone() {
        return economicZone;
    }

    public void setEconomicZone(String economicZone) {
        this.economicZone = economicZone;
    }

    public String getListedCompany() {
        return listedCompany;
    }

    public void setListedCompany(String listedCompany) {
        this.listedCompany = listedCompany;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getOrganizationTypeDetails() {
        return organizationTypeDetails;
    }

    public void setOrganizationTypeDetails(String organizationTypeDetails) {
        this.organizationTypeDetails = organizationTypeDetails;
    }

    public String getCreditCustomer() {
        return creditCustomer;
    }

    public void setCreditCustomer(String creditCustomer) {
        this.creditCustomer = creditCustomer;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getBpFinancialType() {
        return bpFinancialType;
    }

    public void setBpFinancialType(String bpFinancialType) {
        this.bpFinancialType = bpFinancialType;
    }

    public String getSwift() {
        return swift;
    }

    public void setSwift(String swift) {
        this.swift = swift;
    }

    public String getLocalFinance() {
        return localFinance;
    }

    public void setLocalFinance(String localFinance) {
        this.localFinance = localFinance;
    }

    public String getExitFinancing() {
        return exitFinancing;
    }

    public void setExitFinancing(String exitFinancing) {
        this.exitFinancing = exitFinancing;
    }

    public String getBpCategroy() {
        return bpCategroy;
    }

    public void setBpCategroy(String bpCategroy) {
        this.bpCategroy = bpCategroy;
    }

    public String getBpIdN() {
        return bpIdN;
    }

    public void setBpIdN(String bpIdN) {
        this.bpIdN = bpIdN;
    }

    public String getGroupMembership() {
        return groupMembership;
    }

    public void setGroupMembership(String groupMembership) {
        this.groupMembership = groupMembership;
    }

    public String getMarketingReportId() {
        return marketingReportId;
    }

    public void setMarketingReportId(String marketingReportId) {
        this.marketingReportId = marketingReportId;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getBpApproveStatus() {
        return bpApproveStatus;
    }

    public void setBpApproveStatus(String bpApproveStatus) {
        this.bpApproveStatus = bpApproveStatus;
    }

    public String getWorkingCompany() {
        return workingCompany;
    }

    public void setWorkingCompany(String workingCompany) {
        this.workingCompany = workingCompany;
    }
}
