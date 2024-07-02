package com.hand.hls.bp.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
public class HlsBpMaster extends BaseDTO {
    @Id
    @GeneratedValue
    private Long bpId;
    private String bpCode;
    private String bpName;
    private String bpClass;
    private String extraNam;
    private String externalBpCode;
    private String authorityRuleString;
    @Transient
    private Long userId;
    @Transient
    private String bpType;
    @Transient
    private String bpTypeDesc;
    @Transient
    private String bpCategory;
    @Transient
    private String bpCategoryDesc;
    @Transient
    private String typeDes;
    @Transient
    private String cateDes;
    @Transient
    private String bpTypes;
    @Transient
    private String count;
    private String enabledFlag;
    @Transient
    private String collectFlag;
    private String enterpriseScale;
    private String legalForm;
    private String industry;
    private String legalRepresentative;
    private String actualController;
    private String registeredPlace;
    private Date registeredDate;
    private String registeredCapital;
    private String paidInCapital;
    private String enterpriseUnifiedCreditCode;
    private Date foundedDate;
    private String businessLicenseNum;
    private String organizationCode;
    private String taxRegistryNum;
    private String loanCardNum;
    private String loanCardPassword;
    private String primaryBusiness;
    private String mainProducts;
    private String companyInformation;
    private String taxpayerType;
    private String invoiceTitle;
    private String invoiceBpAddressPhoneNum;
    private String invoiceBpBankAccount;
    private String nationality;
    private Date dateOfBirth;
    private String nativePlace;
    @Transient
    private String existFlag;

    public String getExistFlag() {
        return existFlag;
    }

    public void setExistFlag(String existFlag) {
        this.existFlag = existFlag;
    }

    private String regno;

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    private String idType;
    private String idCardNo;
    private String maritalStatus;
    private String academicBackground;
    private Long age;
    private String gender;
    private Double annualIncome;
    private String workingCompany;
    private String companyIndustry;
    private String businessJob;
    private Long workingYears;
    private String phone;
    private String cellPhone;
    private String email;
    @Transient
    private String contractId;
    @Transient
    private String idTypeDesc;
    @Transient
    private String bpClassDesc;
    @Transient
    private String description;
    @Transient
    private Date creationDate;
    @Transient
    private Date lastUpdateDate;
    @Transient
    private String allCount;
    @Transient
    private String monthAddCount;
    @Transient
    private String createBy;
    @Transient
    private String contactPerson;
    @Transient
    private String bpPosition;
    @Transient
    private String bpTypeEn;
    @Transient
    private String bpNameString;
    @Transient
    private String bankName;
    @Transient
    private String bankShortName;
    @Transient
    private String orgType;
    @Transient
    private Date startActiveDate;
    @Transient
    private Date endActiveDate;
    @Transient
    private String bankCode;
    @Transient
    private Long bankId;
    @Transient
    private Long projectId;
    @Transient
    private String industryDesc;

    private String easBpCode;

    public HlsBpMaster() {
    }

    public String getEasBpCode() {
        return easBpCode;
    }

    public void setEasBpCode(String easBpCode) {
        this.easBpCode = easBpCode;
    }

    public String getCateDes() {
        return this.cateDes;
    }

    public void setCateDes(String cateDes) {
        this.cateDes = cateDes;
    }

    public String getTypeDes() {
        return this.typeDes;
    }

    public void setTypeDes(String typeDes) {
        this.typeDes = typeDes;
    }

    public String getBpCategory() {
        return this.bpCategory;
    }

    public void setBpCategory(String bpCategory) {
        this.bpCategory = bpCategory;
    }

    public String getBpTypes() {
        return this.bpTypes;
    }

    public void setBpTypes(String bpTypes) {
        this.bpTypes = bpTypes;
    }

    public String getIndustry() {
        return this.industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getIndustryDesc() {
        return this.industryDesc;
    }

    public void setIndustryDesc(String industryDesc) {
        this.industryDesc = industryDesc;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getBankId() {
        return this.bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBpTypeEn() {
        return this.bpTypeEn;
    }

    public void setBpTypeEn(String bpTypeEn) {
        this.bpTypeEn = bpTypeEn;
    }

    public String getBpPosition() {
        return this.bpPosition;
    }

    public void setBpPosition(String bpPosition) {
        this.bpPosition = bpPosition;
    }

    public String getContactPerson() {
        return this.contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getAllCount() {
        return this.allCount;
    }

    public void setAllCount(String allCount) {
        this.allCount = allCount;
    }

    public String getMonthAddCount() {
        return this.monthAddCount;
    }

    public void setMonthAddCount(String monthAddCount) {
        this.monthAddCount = monthAddCount;
    }

    public Date getCreationDate() {
        return this.creationDate;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdTypeDesc() {
        return this.idTypeDesc;
    }

    public void setIdTypeDesc(String idTypeDesc) {
        this.idTypeDesc = idTypeDesc;
    }

    public String getBpClassDesc() {
        return this.bpClassDesc;
    }

    public void setBpClassDesc(String bpClassDesc) {
        this.bpClassDesc = bpClassDesc;
    }

    public String getEnterpriseScale() {
        return this.enterpriseScale;
    }

    public void setEnterpriseScale(String enterpriseScale) {
        this.enterpriseScale = enterpriseScale;
    }

    public String getLegalForm() {
        return this.legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    public String getLegalRepresentative() {
        return this.legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getActualController() {
        return this.actualController;
    }

    public void setActualController(String actualController) {
        this.actualController = actualController;
    }

    public String getRegisteredPlace() {
        return this.registeredPlace;
    }

    public void setRegisteredPlace(String registeredPlace) {
        this.registeredPlace = registeredPlace;
    }

    public Date getRegisteredDate() {
        return this.registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getRegisteredCapital() {
        return this.registeredCapital;
    }

    public void setRegisteredCapital(String registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    public String getPaidInCapital() {
        return this.paidInCapital;
    }

    public void setPaidInCapital(String paidInCapital) {
        this.paidInCapital = paidInCapital;
    }

    public String getEnterpriseUnifiedCreditCode() {
        return this.enterpriseUnifiedCreditCode;
    }

    public void setEnterpriseUnifiedCreditCode(String enterpriseUnifiedCreditCode) {
        this.enterpriseUnifiedCreditCode = enterpriseUnifiedCreditCode;
    }

    public Date getFoundedDate() {
        return this.foundedDate;
    }

    public void setFoundedDate(Date foundedDate) {
        this.foundedDate = foundedDate;
    }

    public String getBusinessLicenseNum() {
        return this.businessLicenseNum;
    }

    public void setBusinessLicenseNum(String businessLicenseNum) {
        this.businessLicenseNum = businessLicenseNum;
    }

    public String getOrganizationCode() {
        return this.organizationCode;
    }

    public String getContractId() {
        return this.contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
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

    public String getLoanCardNum() {
        return this.loanCardNum;
    }

    public void setLoanCardNum(String loanCardNum) {
        this.loanCardNum = loanCardNum;
    }

    public String getLoanCardPassword() {
        return this.loanCardPassword;
    }

    public void setLoanCardPassword(String loanCardPassword) {
        this.loanCardPassword = loanCardPassword;
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

    public String getCompanyInformation() {
        return this.companyInformation;
    }

    public void setCompanyInformation(String companyInformation) {
        this.companyInformation = companyInformation;
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

    public String getBpNameString() {
        return this.bpNameString;
    }

    public void setBpNameString(String bpNameString) {
        this.bpNameString = bpNameString;
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

    public String getNativePlace() {
        return this.nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
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

    public String getMaritalStatus() {
        return this.maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
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

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getAnnualIncome() {
        return this.annualIncome;
    }

    public void setAnnualIncome(Double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getWorkingCompany() {
        return this.workingCompany;
    }

    public void setWorkingCompany(String workingCompany) {
        this.workingCompany = workingCompany;
    }

    public String getCompanyIndustry() {
        return this.companyIndustry;
    }

    public void setCompanyIndustry(String companyIndustry) {
        this.companyIndustry = companyIndustry;
    }

    public String getBusinessJob() {
        return this.businessJob;
    }

    public void setBusinessJob(String businessJob) {
        this.businessJob = businessJob;
    }

    public Long getWorkingYears() {
        return this.workingYears;
    }

    public void setWorkingYears(Long workingYears) {
        this.workingYears = workingYears;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCellPhone() {
        return this.cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCollectFlag() {
        return this.collectFlag;
    }

    public String getBpType() {
        return this.bpType;
    }

    public void setBpType(String bpType) {
        this.bpType = bpType;
    }

    public void setCollectFlag(String collectFlag) {
        this.collectFlag = collectFlag;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getBpCode() {
        return this.bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode == null ? null : bpCode.trim();
    }

    public String getBpName() {
        return this.bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName == null ? null : bpName.trim();
    }

    public String getBpClass() {
        return this.bpClass;
    }

    public void setBpClass(String bpClass) {
        this.bpClass = bpClass == null ? null : bpClass.trim();
    }

    public String getExtraNam() {
        return this.extraNam;
    }

    public void setExtraNam(String extraNam) {
        this.extraNam = extraNam == null ? null : extraNam.trim();
    }

    public String getExternalBpCode() {
        return this.externalBpCode;
    }

    public void setExternalBpCode(String externalBpCode) {
        this.externalBpCode = externalBpCode == null ? null : externalBpCode.trim();
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag == null ? null : enabledFlag.trim();
    }

    public String getCount() {
        return this.count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getBpTypeDesc() {
        return this.bpTypeDesc;
    }

    public void setBpTypeDesc(String bpTypeDesc) {
        this.bpTypeDesc = bpTypeDesc;
    }

    public String getBankName() {
        return this.bankName;
    }

    public String getBankShortName() {
        return this.bankShortName;
    }

    public String getOrgType() {
        return this.orgType;
    }

    public Date getStartActiveDate() {
        return this.startActiveDate;
    }

    public Date getEndActiveDate() {
        return this.endActiveDate;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public void setStartActiveDate(Date startActiveDate) {
        this.startActiveDate = startActiveDate;
    }

    public void setEndActiveDate(Date endActiveDate) {
        this.endActiveDate = endActiveDate;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getAuthorityRuleString() {
        return this.authorityRuleString;
    }

    public void setAuthorityRuleString(String authorityRuleString) {
        this.authorityRuleString = authorityRuleString;
    }

    public String getBpCategoryDesc() {
        return this.bpCategoryDesc;
    }

    public void setBpCategoryDesc(String bpCategoryDesc) {
        this.bpCategoryDesc = bpCategoryDesc;
    }
}