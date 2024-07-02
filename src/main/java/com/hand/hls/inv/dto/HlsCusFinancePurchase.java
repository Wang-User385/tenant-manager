package com.hand.hls.inv.dto;

/**
 * @Description:申购信息
 * @Author: wty
 * @Date: Created in 14:45 2018/4/17
 */

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@ExtensionAttribute(disable = true)
@Table(name = "inv_finance_purchase")
public class HlsCusFinancePurchase extends BaseDTO {

    public static final String FIELD_FINANCE_PURCHASE_ID = "financePurchaseId";
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_DOCUMENT_TYPE = "documentType";
    public static final String FIELD_DOCUMENT_CATEGORY = "documentCategory";
    public static final String FIELD_BUSINESS_TYPE = "businessType";
    public static final String FIELD_BANK_ACCOUNT_ID = "bankAccountId";
    public static final String FIELD_ISSUING_AGENCY = "issuingAgency";
    public static final String FIELD_PURCHASE_NUMBER = "purchaseNumber";
    public static final String FIELD_PURCHASE_APPLICANT = "purchaseApplicant";
    public static final String FIELD_INVESTMENT_AMOUNT = "investmentAmount";
    public static final String FIELD_CURRENCY = "currency";
    public static final String FIELD_FINANCIAL_TYPE = "financialType";
    public static final String FIELD_FINANCIAL_CATEGORY = "financialCategory";
    public static final String FIELD_ANNUAL_RATE = "annualRate";
    public static final String FIELD_VALUE_DATE = "valueDate";
    public static final String FIELD_EXPECTED_DUE_DATE = "expectedDueDate";
    public static final String FIELD_NEW_OR_ADD = "newOrAdd";
    public static final String FIELD_FINANCIAL_PRODUCT_NAME = "financialProductName";
    public static final String FIELD_NOTE = "note";
    public static final String FIELD_PURCHASE_STATUS = "purchaseStatus";
    public static final String FIELD_PURCHASE_DATE = "purchaseDate";
    public static final String FIELD_ALL_APPROVED = "allApproved";

    public static final String FIELD_FIRST_NEW = "firstNew";//首次新建
    public static final String FIELD_FIRST_APPROVING = "firstApproving";//首次审批中
    public static final String FIELD_FIRST_RETURN = "firstReturn";//首次审批退回
    public static final String FIELD_FIRST_APPROVED = "firstApproved";//首次审批完成
    public static final String FIELD_ADD_NEW = "addNew";//追加新建
    public static final String FIELD_ADD_APPROVING = "addApproving";//追加审批中
    public static final String FIELD_ADD_RETURN = "addReturn";//追加退回
    public static final String FIELD_ADD_APPROVED = "addApproved";//追加审批完成
    public static final String FIELD_REDEEM_NEW = "redeemNew";//赎回新建
    public static final String FIELD_REDEEM_APPROVING = "redeemApproving";//赎回审批中
    public static final String FIELD_REDEEM_RETURN = "redeemReturn";//赎回退回
    public static final String FIELD_REDEEM_APPROVED = "redeemApproved";//赎回审批完成


    @Id
    @GeneratedValue
    private Long financePurchaseId; //申购信息id

    @NotNull
    private Long companyId; //公司id

    @Length(max = 100)
    private String documentType; //单据类型

    @Length(max = 100)
    private String documentCategory; //单据类别

    @Length(max = 100)
    private String businessType; //业务类型

    private Long bankAccountId; //银行账户id

    @Length(max = 255)
    private String issuingAgency; //发行机构

    @Length(max = 100)
    private String purchaseNumber; //申请编号

    private Long purchaseApplicant; //申购人

    private Double investmentAmount; //投资金额

    @Length(max = 20)
    private String currency; //币种

    @Length(max = 100)
    private String financialType; //理财类型

    @Length(max = 100)
    private String financialCategory; //理财类别

    private String annualRate; //年收益率

    private Date valueDate; //起息日

    private Date expectedDueDate; //预计到息日

    @Length(max = 20)
    private String newOrAdd; //新建追加

    @Length(max = 255)
    private String financialProductName; //产品名称

    @Length(max = 1000)
    private String note; //备注

    @Length(max = 100)
    private String purchaseStatus; //申购状态

    private Date purchaseDate;//申购日期

    @Length(max = 20)
    private String allApproved;//是否全部赎回

    private Long parentPurchaseId;//父级申购id

    private Long detailDateId;//起息日到息日取值id

    private double amountBeforeChange;//金额变更前

    private double amountAfterChange;//金额变更后

    private String pledgeFlag;   // 是否质押

    private String pledgeDeadline;   // 质押期限

    @Length(max = 255)
    private String invalidBak;

    @Length(max = 2000)
    private String invalidMessage;

    @Transient
    private String companyName;//公司名称

    @Transient
    private String bankBranchName;//银行分行名称

    @Transient
    private String bankAccountNum;//银行账户

    @Transient
    private String purchaseApplicantName;//申购人姓名

    @Transient
    private String bankAccountName;//银行账户名

    @Transient
    private String bankName;//银行名称

    @Transient
    private String bank;

    @Transient
    private String documentDetailStatus;//单据具体类型,如申购新建，赎回审批中等

    @Transient
    private Double thisPurchaseAmount;//本次申购金额

    @Transient
    private Double thisRedempetionAmount;//本次赎回金额

    @Transient
    private Date thisPurchaseDate;//本次申购日期

    @Transient
    private Date thisRedempetionDate;//本次赎回日期

    @Transient
    private String iRedeemStatus;//是否赎回:UNREDEEMED  未赎回,PARTREDEEMED  部分赎回, REDEEMED 全部赎回

    @Transient
    private String firstPurchaseUserName;//第一次申购人姓名

    @Transient
    private String lastPurchaseUserName;//最后一次申购人姓名

    @Transient
    private Long purchaseFirstUserId; //首次申购人id

    @Transient
    private Date purchaseFirstDate; //首次申购日期

    @Transient
    private Long purchaseLastUserId; //最后申购人id

    @Transient
    private Date purchaseLastDate; //最后申购日期

    @Transient
    private Double purchaseRedeemedAmount; //申购已赎回金额

    @Transient
    private Double purchaseUnredeemedAmount; //申购未赎回金额

    @Transient
    private String whetherChange;//是否能够变更

    @Transient
    private Long financialTypeCount;//理财类型数量

    @Transient
    private Long allFinancialTypeCount;//所有理财类型数量

    @Transient
    private Double allInvestmentAmount;//总投资金额

    @Transient
    private Long typeCountPercent;//理财类型占比

    @Transient
    private Long inverstmentAmountPercent;//投资金额占比

    @Transient
    private String financialTypes;//理财类型s

    @Transient
    private String redeemTypes;//赎回类型s

    @Transient
    private String financialCategories;//理财类别s

    @Transient
    private String[] financialCategoryStatus;

    @Transient
    private String[] financialTypeStatus;

    @Transient
    private String[] redeemTypeStatus;

    @Transient
    private Double investmentAmountFrom;//投资金额从

    @Transient
    private Double investmentAmountTo;//投资金额到

    @Transient
    private Double purchaseUnredeemedAmountFrom;//为赎回金额从

    @Transient
    private Double purchaseUnredeemedAmountTo;//未赎回金额到

    @Transient
    private String purchaseDateFrom;//本次申购赎回日期从

    @Transient
    private String purchaseDateTo;//本次申购赎回日期到

    @Transient
    private String valueDateFrom;//起息日从

    @Transient
    private String valueDateTo;//起息日到

    @Transient
    private String expectedDueDateFrom;//到息日从

    @Transient
    private String expectedDueDateTo;//到息日到

    @Transient
    private Long days;
    @Transient
    private String isLimitedN;

    @Transient
    private HlsCusFinancePurchaseBak hlsCusFinancePurchaseBak;

    @Transient
    private List<HlsCusInvFinanceDetail> hlsCusInvFinanceDetailList;

    public String getPledgeFlag() {
        return pledgeFlag;
    }

    public void setPledgeFlag(String pledgeFlag) {
        this.pledgeFlag = pledgeFlag;
    }

    public String getPledgeDeadline() {
        return pledgeDeadline;
    }

    public void setPledgeDeadline(String pledgeDeadline) {
        this.pledgeDeadline = pledgeDeadline;
    }

    public HlsCusFinancePurchaseBak getHlsCusFinancePurchaseBak() {
        return hlsCusFinancePurchaseBak;
    }

    public void setHlsCusFinancePurchaseBak(HlsCusFinancePurchaseBak hlsCusFinancePurchaseBak) {
        this.hlsCusFinancePurchaseBak = hlsCusFinancePurchaseBak;
    }

    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }

    public String getInvalidBak() {
        return invalidBak;
    }

    public void setInvalidBak(String invalidBak) {
        this.invalidBak = invalidBak;
    }

    public String getInvalidMessage() {
        return invalidMessage;
    }

    public void setInvalidMessage(String invalidMessage) {
        this.invalidMessage = invalidMessage;
    }

    public Date getThisPurchaseDate() {
        return thisPurchaseDate;
    }

    public void setThisPurchaseDate(Date thisPurchaseDate) {
        this.thisPurchaseDate = thisPurchaseDate;
    }

    public Date getThisRedempetionDate() {
        return thisRedempetionDate;
    }

    public void setThisRedempetionDate(Date thisRedempetionDate) {
        this.thisRedempetionDate = thisRedempetionDate;
    }

    public String getFinancialCategories() {
        return financialCategories;
    }

    public void setFinancialCategories(String financialCategories) {
        this.financialCategories = financialCategories;
    }

    public String[] getFinancialCategoryStatus() {
        return financialCategoryStatus;
    }

    public void setFinancialCategoryStatus(String[] financialCategoryStatus) {
        this.financialCategoryStatus = financialCategoryStatus;
    }

    public Double getInvestmentAmountFrom() {
        return investmentAmountFrom;
    }

    public void setInvestmentAmountFrom(Double investmentAmountFrom) {
        this.investmentAmountFrom = investmentAmountFrom;
    }

    public Double getInvestmentAmountTo() {
        return investmentAmountTo;
    }

    public void setInvestmentAmountTo(Double investmentAmountTo) {
        this.investmentAmountTo = investmentAmountTo;
    }

    public Double getPurchaseUnredeemedAmountFrom() {
        return purchaseUnredeemedAmountFrom;
    }

    public void setPurchaseUnredeemedAmountFrom(Double purchaseUnredeemedAmountFrom) {
        this.purchaseUnredeemedAmountFrom = purchaseUnredeemedAmountFrom;
    }

    public Double getPurchaseUnredeemedAmountTo() {
        return purchaseUnredeemedAmountTo;
    }

    public void setPurchaseUnredeemedAmountTo(Double purchaseUnredeemedAmountTo) {
        this.purchaseUnredeemedAmountTo = purchaseUnredeemedAmountTo;
    }

    public String getPurchaseDateFrom() {
        return purchaseDateFrom;
    }

    public void setPurchaseDateFrom(String purchaseDateFrom) {
        this.purchaseDateFrom = purchaseDateFrom;
    }

    public String getPurchaseDateTo() {
        return purchaseDateTo;
    }

    public void setPurchaseDateTo(String purchaseDateTo) {
        this.purchaseDateTo = purchaseDateTo;
    }

    public String getValueDateFrom() {
        return valueDateFrom;
    }

    public void setValueDateFrom(String valueDateFrom) {
        this.valueDateFrom = valueDateFrom;
    }

    public String getValueDateTo() {
        return valueDateTo;
    }

    public void setValueDateTo(String valueDateTo) {
        this.valueDateTo = valueDateTo;
    }

    public String getExpectedDueDateFrom() {
        return expectedDueDateFrom;
    }

    public void setExpectedDueDateFrom(String expectedDueDateFrom) {
        this.expectedDueDateFrom = expectedDueDateFrom;
    }

    public String getExpectedDueDateTo() {
        return expectedDueDateTo;
    }

    public void setExpectedDueDateTo(String expectedDueDateTo) {
        this.expectedDueDateTo = expectedDueDateTo;
    }

    public String getFinancialTypes() {
        return financialTypes;
    }

    public void setFinancialTypes(String financialTypes) {
        this.financialTypes = financialTypes;
    }

    public String getRedeemTypes() {
        return redeemTypes;
    }

    public void setRedeemTypes(String redeemTypes) {
        this.redeemTypes = redeemTypes;
    }

    public String[] getFinancialTypeStatus() {
        return financialTypeStatus;
    }

    public void setFinancialTypeStatus(String[] financialTypeStatus) {
        this.financialTypeStatus = financialTypeStatus;
    }

    public String[] getRedeemTypeStatus() {
        return redeemTypeStatus;
    }

    public void setRedeemTypeStatus(String[] redeemTypeStatus) {
        this.redeemTypeStatus = redeemTypeStatus;
    }

    public Long getFinancialTypeCount() {
        return financialTypeCount;
    }

    public void setFinancialTypeCount(Long financialTypeCount) {
        this.financialTypeCount = financialTypeCount;
    }

    public Long getAllFinancialTypeCount() {
        return allFinancialTypeCount;
    }

    public void setAllFinancialTypeCount(Long allFinancialTypeCount) {
        this.allFinancialTypeCount = allFinancialTypeCount;
    }

    public Double getAllInvestmentAmount() {
        return allInvestmentAmount;
    }

    public void setAllInvestmentAmount(Double allInvestmentAmount) {
        this.allInvestmentAmount = allInvestmentAmount;
    }

    public Long getTypeCountPercent() {
        return typeCountPercent;
    }

    public void setTypeCountPercent(Long typeCountPercent) {
        this.typeCountPercent = typeCountPercent;
    }

    public Long getInverstmentAmountPercent() {
        return inverstmentAmountPercent;
    }

    public void setInverstmentAmountPercent(Long inverstmentAmountPercent) {
        this.inverstmentAmountPercent = inverstmentAmountPercent;
    }

    public String getWhetherChange() {
        return whetherChange;
    }

    public void setWhetherChange(String whetherChange) {
        this.whetherChange = whetherChange;
    }

    public Double getThisPurchaseAmount() {
        return thisPurchaseAmount;
    }

    public void setThisPurchaseAmount(Double thisPurchaseAmount) {
        this.thisPurchaseAmount = thisPurchaseAmount;
    }

    public Double getThisRedempetionAmount() {
        return thisRedempetionAmount;
    }

    public void setThisRedempetionAmount(Double thisRedempetionAmount) {
        this.thisRedempetionAmount = thisRedempetionAmount;
    }

    public String getFirstPurchaseUserName() {
        return firstPurchaseUserName;
    }

    public void setFirstPurchaseUserName(String firstPurchaseUserName) {
        this.firstPurchaseUserName = firstPurchaseUserName;
    }

    public String getLastPurchaseUserName() {
        return lastPurchaseUserName;
    }

    public void setLastPurchaseUserName(String lastPurchaseUserName) {
        this.lastPurchaseUserName = lastPurchaseUserName;
    }

    public Long getPurchaseFirstUserId() {
        return purchaseFirstUserId;
    }

    public void setPurchaseFirstUserId(Long purchaseFirstUserId) {
        this.purchaseFirstUserId = purchaseFirstUserId;
    }

    public Date getPurchaseFirstDate() {
        return purchaseFirstDate;
    }

    public void setPurchaseFirstDate(Date purchaseFirstDate) {
        this.purchaseFirstDate = purchaseFirstDate;
    }

    public Long getPurchaseLastUserId() {
        return purchaseLastUserId;
    }

    public void setPurchaseLastUserId(Long purchaseLastUserId) {
        this.purchaseLastUserId = purchaseLastUserId;
    }

    public Date getPurchaseLastDate() {
        return purchaseLastDate;
    }

    public void setPurchaseLastDate(Date purchaseLastDate) {
        this.purchaseLastDate = purchaseLastDate;
    }

    public Double getPurchaseRedeemedAmount() {
        return purchaseRedeemedAmount;
    }

    public void setPurchaseRedeemedAmount(Double purchaseRedeemedAmount) {
        this.purchaseRedeemedAmount = purchaseRedeemedAmount;
    }

    public Double getPurchaseUnredeemedAmount() {
        return purchaseUnredeemedAmount;
    }

    public void setPurchaseUnredeemedAmount(Double purchaseUnredeemedAmount) {
        this.purchaseUnredeemedAmount = purchaseUnredeemedAmount;
    }

    public double getAmountBeforeChange() {
        return amountBeforeChange;
    }

    public void setAmountBeforeChange(double amountBeforeChange) {
        this.amountBeforeChange = amountBeforeChange;
    }

    public double getAmountAfterChange() {
        return amountAfterChange;
    }

    public void setAmountAfterChange(double amountAfterChange) {
        this.amountAfterChange = amountAfterChange;
    }

    public Long getParentPurchaseId() {
        return parentPurchaseId;
    }

    public void setParentPurchaseId(Long parentPurchaseId) {
        this.parentPurchaseId = parentPurchaseId;
    }

    public Long getDetailDateId() {
        return detailDateId;
    }

    public void setDetailDateId(Long detailDateId) {
        this.detailDateId = detailDateId;
    }

    public String getDocumentDetailStatus() {
        return documentDetailStatus;
    }

    public void setDocumentDetailStatus(String documentDetailStatus) {
        this.documentDetailStatus = documentDetailStatus;
    }

    public String getiRedeemStatus() {
        return iRedeemStatus;
    }

    public void setiRedeemStatus(String iRedeemStatus) {
        this.iRedeemStatus = iRedeemStatus;
    }


    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public void setFinancePurchaseId(Long financePurchaseId) {
        this.financePurchaseId = financePurchaseId;
    }

    public Long getFinancePurchaseId() {
        return financePurchaseId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public String getDocumentCategory() {
        return documentCategory;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setIssuingAgency(String issuingAgency) {
        this.issuingAgency = issuingAgency;
    }

    public String getIssuingAgency() {
        return issuingAgency;
    }

    public void setPurchaseNumber(String purchaseNumber) {
        this.purchaseNumber = purchaseNumber;
    }

    public String getPurchaseNumber() {
        return purchaseNumber;
    }

    public void setPurchaseApplicant(Long purchaseApplicant) {
        this.purchaseApplicant = purchaseApplicant;
    }

    public Long getPurchaseApplicant() {
        return purchaseApplicant;
    }

    public void setInvestmentAmount(Double investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public Double getInvestmentAmount() {
        return investmentAmount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setFinancialType(String financialType) {
        this.financialType = financialType;
    }

    public String getFinancialType() {
        return financialType;
    }

    public void setFinancialCategory(String financialCategory) {
        this.financialCategory = financialCategory;
    }

    public String getFinancialCategory() {
        return financialCategory;
    }

    public void setAnnualRate(String annualRate) {
        this.annualRate = annualRate;
    }

    public String getAnnualRate() {
        return annualRate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setExpectedDueDate(Date expectedDueDate) {
        this.expectedDueDate = expectedDueDate;
    }

    public Date getExpectedDueDate() {
        return expectedDueDate;
    }

    public void setNewOrAdd(String newOrAdd) {
        this.newOrAdd = newOrAdd;
    }

    public String getNewOrAdd() {
        return newOrAdd;
    }

    public void setFinancialProductName(String financialProductName) {
        this.financialProductName = financialProductName;
    }

    public String getFinancialProductName() {
        return financialProductName;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setPurchaseStatus(String purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }

    public String getPurchaseStatus() {
        return purchaseStatus;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public String getBankAccountNum() {
        return bankAccountNum;
    }

    public void setBankAccountNum(String bankAccountNum) {
        this.bankAccountNum = bankAccountNum;
    }

    public String getPurchaseApplicantName() {
        return purchaseApplicantName;
    }

    public void setPurchaseApplicantName(String purchaseApplicantName) {
        this.purchaseApplicantName = purchaseApplicantName;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getAllApproved() {
        return allApproved;
    }

    public void setAllApproved(String allApproved) {
        this.allApproved = allApproved;
    }

    private Long paymentBankAccountId;//收款银行账户ID

    private Date lastPurchaseDate;//最晚申购日期

    private String isLimited;//是否受限

    private Long invBankId;//银行发行机构ID

    private Long purchaseUnit;//申购部门

    private Date purchaseCreatedDate;//申购创建日期

    private Long financeProductsId;//理财产品ID

    @Transient
    private String paymentBankName;//收款银行

    @Transient
    private String paymentBankAccountName;//收款银行账户

    @Transient
    private String paymentBankAccountNum;//收款银行账号

    @Transient
    private String purchaseUnitName;//申购部门

    @Transient
    private String invBankName;//发行机构银行名称

    @Transient
    private Double financialMaxAmount;//最高限额

    @Transient
    private String financialProductsNum;//产品代码

    @Transient
    private String financialProductsName;//产品名称

    @Transient
    private Long invProductsId;//首页产品分组ID

    @Transient
    private String currencyDesc;//币种

    @Transient
    private String invBankNameN;

    @Transient
    private String financialTypeN;
    @Transient
    private String iredeemStatusN;

    @Transient
    private String financialCategoryN;

    @Transient
    private String currencyN;

    public Long getPayment_bank_account_id() {
        return paymentBankAccountId;
    }

    public void setPaymentBankAccountId(Long paymentBankAccountId) {
        this.paymentBankAccountId = paymentBankAccountId;
    }

    public Date getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setLastPurchaseDate(Date lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }

    public String getIsLimited() {
        return isLimited;
    }

    public void setIsLimited(String isLimited) {
        this.isLimited = isLimited;
    }

    public Long getInvBankId() {
        return invBankId;
    }

    public void setInvBankId(Long invBankId) {
        this.invBankId = invBankId;
    }

    public String getPaymentBankName() {
        return paymentBankName;
    }

    public void setPaymentBankName(String paymentBankName) {
        this.paymentBankName = paymentBankName;
    }

    public String getPaymentBankAccountName() {
        return paymentBankAccountName;
    }

    public void setPaymentBankAccountName(String paymentBankAccountName) {
        this.paymentBankAccountName = paymentBankAccountName;
    }

    public String getPaymentBankAccountNum() {
        return paymentBankAccountNum;
    }

    public void setPaymentBankAccountNum(String paymentBankAccountNum) {
        this.paymentBankAccountNum = paymentBankAccountNum;
    }

    public Long getPurchaseUnit() {
        return purchaseUnit;
    }

    public void setPurchaseUnit(Long purchaseUnit) {
        this.purchaseUnit = purchaseUnit;
    }

    public Date getPurchaseCreatedDate() {
        return purchaseCreatedDate;
    }

    public void setPurchaseCreatedDate(Date purchaseCreatedDate) {
        this.purchaseCreatedDate = purchaseCreatedDate;
    }

    public String getPurchaseUnitName() {
        return purchaseUnitName;
    }

    public void setPurchaseUnitName(String purchaseUnitName) {
        this.purchaseUnitName = purchaseUnitName;
    }

    public Long getPaymentBankAccountId() {
        return paymentBankAccountId;
    }

    public String getInvBankName() {
        return invBankName;
    }

    public void setInvBankName(String invBankName) {
        this.invBankName = invBankName;
    }

    public Long getFinanceProductsId() {
        return financeProductsId;
    }

    public void setFinanceProductsId(Long financeProductsId) {
        this.financeProductsId = financeProductsId;
    }

    public Double getFinancialMaxAmount() {
        return financialMaxAmount;
    }

    public void setFinancialMaxAmount(Double financialMaxAmount) {
        this.financialMaxAmount = financialMaxAmount;
    }

    public String getFinancialProductsNum() {
        return financialProductsNum;
    }

    public void setFinancialProductsNum(String financialProductsNum) {
        this.financialProductsNum = financialProductsNum;
    }

    public String getFinancialProductsName() {
        return financialProductsName;
    }

    public void setFinancialProductsName(String financialProductsName) {
        this.financialProductsName = financialProductsName;
    }

    public Long getInvProductsId() {
        return invProductsId;
    }

    public void setInvProductsId(Long invProductsId) {
        this.invProductsId = invProductsId;
    }

    public String getCurrencyDesc() {
        return currencyDesc;
    }

    public void setCurrencyDesc(String currencyDesc) {
        this.currencyDesc = currencyDesc;
    }

    private String confirmStatus;//财务确认状态（NOT/PARTIAL/FULL）

    public String getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(String confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public List<HlsCusInvFinanceDetail> getHlsCusInvFinanceDetailList() {
        return hlsCusInvFinanceDetailList;
    }

    public void setHlsCusInvFinanceDetailList(List<HlsCusInvFinanceDetail> hlsCusInvFinanceDetailList) {
        this.hlsCusInvFinanceDetailList = hlsCusInvFinanceDetailList;
    }

    //8月22日新增字段
    private Double purchasedAmount;//已实际申购金额

    private Double redeemedAmount;//已实际赎回金额

    @Transient
    private Double purchasingAmount;//申购中金额

    @Transient
    private Double redeemingAmount;//赎回中金额

    @Transient
    private String approvedStatus;//审批状态

    public Double getPurchasedAmount() {
        return purchasedAmount;
    }

    public void setPurchasedAmount(Double purchasedAmount) {
        this.purchasedAmount = purchasedAmount;
    }

    public Double getRedeemedAmount() {
        return redeemedAmount;
    }

    public void setRedeemedAmount(Double redeemedAmount) {
        this.redeemedAmount = redeemedAmount;
    }

    public Double getPurchasingAmount() {
        return purchasingAmount;
    }

    public void setPurchasingAmount(Double purchasingAmount) {
        this.purchasingAmount = purchasingAmount;
    }

    public Double getRedeemingAmount() {
        return redeemingAmount;
    }

    public void setRedeemingAmount(Double redeemingAmount) {
        this.redeemingAmount = redeemingAmount;
    }

    public String getApprovedStatus() {
        return approvedStatus;
    }

    public void setApprovedStatus(String approvedStatus) {
        this.approvedStatus = approvedStatus;
    }

    @Transient
    private String dataClass;//类型

    @Transient
    private Long financeRedeemId;//赎回ID

    public String getDataClass() {
        return dataClass;
    }

    public void setDataClass(String dataClass) {
        this.dataClass = dataClass;
    }

    public Long getFinanceRedeemId() {
        return financeRedeemId;
    }

    public void setFinanceRedeemId(Long financeRedeemId) {
        this.financeRedeemId = financeRedeemId;
    }

    public String getInvBankNameN() {
        return invBankNameN;
    }

    public void setInvBankNameN(String invBankNameN) {
        this.invBankNameN = invBankNameN;
    }

    public String getFinancialTypeN() {
        return financialTypeN;
    }

    public void setFinancialTypeN(String financialTypeN) {
        this.financialTypeN = financialTypeN;
    }

    public String getFinancialCategoryN() {
        return financialCategoryN;
    }

    public void setFinancialCategoryN(String financialCategoryN) {
        this.financialCategoryN = financialCategoryN;
    }

    public String getCurrencyN() {
        return currencyN;
    }

    public void setCurrencyN(String currencyN) {
        this.currencyN = currencyN;
    }

    public String getIredeemStatusN() {
        return iredeemStatusN;
    }

    public void setIredeemStatusN(String iredeemStatusN) {
        this.iredeemStatusN = iredeemStatusN;
    }

    public String getIsLimitedN() {
        return isLimitedN;
    }

    public void setIsLimitedN(String isLimitedN) {
        this.isLimitedN = isLimitedN;
    }
}
