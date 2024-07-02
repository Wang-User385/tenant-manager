package com.hand.hls.fin.dto;

/**
 * add by zhangyu 20180417
 **/

import com.hand.hap.core.annotation.Children;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@ExtensionAttribute(disable = true)
@Table(name = "lon_credit_contract")
public class HlsCusCreditContract extends CreditContract {

    //public static final String FIELD_CREDIT_CONTRACT_ID = "creditContractId";
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_DOCUMENT_TYPE = "documentType";
    public static final String FIELD_DOCUMENT_CATEGORY = "documentCategory";
    public static final String FIELD_BUSINESS_TYPE = "businessType";
    public static final String FIELD_CREDIT_CONTRACT_NUMBER = "creditContractNumber";
    public static final String FIELD_CREDIT_CONTRACT_NAME = "creditContractName";
    public static final String FIELD_CREDIT_CONTRACT_STATUS = "creditContractStatus";
    public static final String FIELD_CREDIT_LINE_ID = "creditLineId";
    public static final String FIELD_CURRENCY = "currency";
    public static final String FIELD_CREDIT_BP_ID = "creditBpId";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_LON_COMPANY_ID = "lonCompanyId";
    public static final String FIELD_FINANCING_CHANNEL = "financingChannel";
    public static final String FIELD_CREDIT_CON_NUMBER = "creditConNumber";
    public static final String FIELD_CREDIT_REPLY_NO = "creditReplyNo";
    public static final String FIELD_CREDIT_CATEGORY = "creditCategory";
    public static final String FIELD_CHANNEL_BUSINESS_FLAG = "channelBusinessFlag";
    public static final String FIELD_REF_CREDIT_BP_ID = "refCreditBpId";

    private Long companyId; //公司ID

    @Length(max = 100)
    private String documentType; //单据类型

    @Length(max = 100)
    private String documentCategory; //单据类别

    @Length(max = 100)
    private String businessType; //业务类型

    @Length(max = 255)
    private String creditContractNumber; //融资授信合同编号

    @Length(max = 500)
    private String creditContractName; //融资授信合同名称

    @Length(max = 100)
    private String creditContractStatus; //融资授信合同状态

    private Long creditLineId; //额度ID
    @Length(max = 100)
    private String currency; //币种

    private Long creditBpId; //授信机构ID

    @Length(max = 500)
    private String description; //说明

    private Long lonCompanyId; //授信主体

    @Length(max = 30)
    private String financingChannel; //融资渠道

    @Length(max = 30)
    private String creditConNumber; //授信编码

    @Length(max = 200)
    private String creditReplyNo; //授信批复号

    @Length(max = 30)
    private String creditCategory; //额度类别

    @Length(max = 1)
    private String channelBusinessFlag; //是否通道业务

    private Long refCreditBpId;

    @Length(max = 30)
    private String dataClass; //数据类型

    @Length(max = 30)
    private String confirmStatus; //确认状态

    private Long lastChangeReqId; //最近一次变更ID
    private Date dateFrom;
    private Date dateTo;

    //总授信额度
    private Double creditLineAmt;

    //已占用额度 (改为动态取数 表字段无效了)
    @Transient
    private Double creditExposureAmt;

    //未占用额度 (改为动态取数 表字段无效了)
    @Transient
    private Double creditUnexposureAmt;

    //提款前提条件
    private String withdrawPerequisite;

    //贷后管理要求
    private String postLoanManage;
    private String quotaType;

    private Long proposerUserId;

    private Long proposerEmployeeAssignId;

    private Long proposerEmployeeId;
    private String enableFlag;

    @Transient
    private String bpName;
    @Transient
    private String creditType;
    @Transient
    private String creditCate;
    @Transient
    private String documentType2;

    @Transient
    private Date validFrom;
    @Transient
    private Date validTo;
    @Transient
    private String bpCategory;
    @Transient
    private Double openToBuy;//可用额度
    @Transient
    private String bpCode;
    @Transient
    private String bpClass;
    @Transient
    private String creditDescription;//额度类型
    @Transient
    private String[] lonTypeStatus; //授信类型
    @Transient
    private String[] creditTypeStatus;//额度类型
    @Transient
    private Double[] creditLineAmtStatus;//额度
    @Transient
    private String lonTypes;
    @Transient
    private String creditTypes;
    @Transient
    private String creditLineAmts;
    @Transient
    private Double maxAmt;
    @Transient
    private Double minAmt;
    @Transient
    private String contractName;
    @Transient
    private Date dueDate;
    @Transient
    private String cfDirection;
    @Transient
    private Long withdrawId;
    @Transient
    private Double writeOffAmount;
    @Transient
    private Long month;
    @Transient
    private Long year;
    @Transient
    private String orgType;
    @Transient
    private Double usedAmt;
    @Transient
    private Double noUsedAmt;
    @Transient
    private Long bpId;
    @Transient
    private String creditLineName;
    @Transient
    private Double withdrawAmt;//还款金额
    @Transient
    private Double repaymentAmt;//借款金额
    @Transient
    private String currencyName;//币种名称
    @Transient
    private String currencyN;//币种名称
    @Transient
    private int revolving;//循环
    @Transient
    private int nonRevolving;//非循环
    @Transient
    private int once;//一次性
    @Transient
    private Double amMax;
    @Transient
    private String bpQueryName;
    @Transient
    private String contractQueryName;
    @Transient
    private String lonDes;
    @Transient
    private String creditDes;
    @Transient
    private String creditCategoryDesc;
    @Transient
    private int exposureCredit;//敞口额度
    @Transient
    private int lowRiskCredit;//低风险额度
    @Transient
    private Double leftAmount;

    @Transient
    private String extraNam;
    @Transient
    private Date delayFrom;
    @Transient
    private Date delayTo;
    @Transient
    private String competentDept;
    @Transient
    private String financeType;
    @Transient
    private Date firstWithdrawDate;
    @Transient
    private String refCreditBpName;
    @Transient
    private String queryDateFrom;
    @Transient
    private String queryDateTo;
    @Transient
    private Double exposureRestAmt;
    @Transient
    private Double exposureUsedAmt;
    @Transient
    private Double lowRiskRestAmt;
    @Transient
    private Double lowRiskUsedAmt;
    @Transient
    private String[] creditCategoryStatus;//额度类别
    @Transient
    private String creditCategorys;
    @Transient
    private String validFromQuery;
    @Transient
    private String validToQuery;
    @Transient
    private String documentTypeDesc;
    @Transient
    private String lonCompanyIdDesc;

    @Transient
    private Long creditYear;

    @Transient
    private Long creditMonth;

    @Transient
    private Long creditDay;

    @Transient
    private Double creditLineAmtLeft;//剩余额度

    @Transient
    private Long contractId;//合同ID

    @Transient
    private String contractNumber;//合同编号

    @Transient
    private String loanTerm;//期限

    @Transient
    private String withdrawNumber;//提款编号

    @Transient
    private Double dueAmount;//提款金额

    @Transient
    private Double financeAmount;//综合成本

    @Transient
    private Double parInterestRate;//票面利率

    @Transient
    private Double chargeFeeRate;

    @Transient
    private Double consultingFeeRate;

    @Transient
    private Double depositRate;

    @Transient
    private Double consignmentSalesFeeRate;

    @Transient
    private Double collocationFeeRate;

    @Transient
    private Double managementFeeRate;

    @Transient
    private Double otherFeeRate;//其他费率

    @Transient
    private String guaranteeMethod;

    @Transient
    private String financePrj;

    /**
     * 提款截止日
     */
    @Transient
    private Date withdrawDeadline;

    /**
     * 创建人
     */
    @Transient
    private String creationName;

    private Date creationDate;

    private Date lastUpdateDate;

    private Long createdBy;

    private Long lastUpdatedBy;

    /**
     * 额度明细List
     */
    @Transient
    @Children
    private List<HlsCusCreditContractLine> creditContractLineList;
    @Transient
    private Long withdrawCount;

    @Transient
    private Double withdrawBalanceSum;

    @Transient
    private Long creditTypeCount;

    @Transient
    private Double  creditAvailableAmtSum;

    @Transient
    private String cancelFlag;


    @Transient
    private Double  withdrawBalance;


    @Transient
    private String bankName;

    @Transient
    private String releComFlag;


    @Transient
    private Long cancelCount;

    @Transient
    private String validFromFormat;

    @Transient
    private String validToFormat;

    /**
     * 总实际可用
     */
    @Transient
    private Double actualAvailableAmtSum;


    /**
     * 是否综合授信
     */
    @Transient
    private String  isMix;


    /**
     * 总累计提款
     */
    @Transient
    private Double totalWithdrawAmountSum;

    /**
     * 总期限内累计提款
     */
    @Transient
    private Double limitWithdrawAmountSum;
    @Transient
    private  String quotaTypeN;

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getQuotaType() {
        return quotaType;
    }

    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getQuotaTypeN() {
        return quotaTypeN;
    }

    public void setQuotaTypeN(String quotaTypeN) {
        this.quotaTypeN = quotaTypeN;
    }

    public Double getLeftAmount() {
        return leftAmount;
    }

    public void setLeftAmount(Double leftAmount) {
        this.leftAmount = leftAmount;
    }

    public String getLonDes() {
        return lonDes;
    }

    public void setLonDes(String lonDes) {
        this.lonDes = lonDes;
    }

    public String getCreditDes() {
        return creditDes;
    }

    public void setCreditDes(String creditDes) {
        this.creditDes = creditDes;
    }

    public String getBpQueryName() {
        return bpQueryName;
    }

    public void setBpQueryName(String bpQueryName) {
        this.bpQueryName = bpQueryName;
    }

    public String getContractQueryName() {
        return contractQueryName;
    }

    public void setContractQueryName(String contractQueryName) {
        this.contractQueryName = contractQueryName;
    }

    public Double getAmMax() {
        return amMax;
    }

    public void setAmMax(Double amMax) {
        this.amMax = amMax;
    }

    public Double[] getCreditLineAmtStatus() {
        return creditLineAmtStatus;
    }

    public void setCreditLineAmtStatus(Double[] creditLineAmtStatus) {
        this.creditLineAmtStatus = creditLineAmtStatus;
    }

    public String getLonTypes() {
        return lonTypes;
    }

    public void setLonTypes(String lonTypes) {
        this.lonTypes = lonTypes;
    }

    public String getCreditTypes() {
        return creditTypes;
    }

    public void setCreditTypes(String creditTypes) {
        this.creditTypes = creditTypes;
    }

    public String getCreditLineAmts() {
        return creditLineAmts;
    }

    public void setCreditLineAmts(String creditLineAmts) {
        this.creditLineAmts = creditLineAmts;
    }

    public String[] getLonTypeStatus() {
        return lonTypeStatus;
    }

    public void setLonTypeStatus(String[] lonTypeStatus) {
        this.lonTypeStatus = lonTypeStatus;
    }

    public String[] getCreditTypeStatus() {
        return creditTypeStatus;
    }

    public void setCreditTypeStatus(String[] creditTypeStatus) {
        this.creditTypeStatus = creditTypeStatus;
    }


    public String getCreditDescription() {
        return creditDescription;
    }

    public void setCreditDescription(String creditDescription) {
        this.creditDescription = creditDescription;
    }


    public int getRevolving() {
        return revolving;
    }

    public void setRevolving(int revolving) {
        this.revolving = revolving;
    }

    public int getNonRevolving() {
        return nonRevolving;
    }

    public void setNonRevolving(int nonRevolving) {
        this.nonRevolving = nonRevolving;
    }

    public int getOnce() {
        return once;
    }

    public void setOnce(int once) {
        this.once = once;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Double getWithdrawAmt() {
        return withdrawAmt;
    }

    public void setWithdrawAmt(Double withdrawAmt) {
        this.withdrawAmt = withdrawAmt;
    }

    public Double getRepaymentAmt() {
        return repaymentAmt;
    }

    public void setRepaymentAmt(Double repaymentAmt) {
        this.repaymentAmt = repaymentAmt;
    }

    public String getCreditLineName() {
        return creditLineName;
    }

    public void setCreditLineName(String creditLineName) {
        this.creditLineName = creditLineName;
    }

    public Long getBpId() {
        return bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Double getUsedAmt() {
        return usedAmt;
    }

    public void setUsedAmt(Double usedAmt) {
        this.usedAmt = usedAmt;
    }

    public Double getNoUsedAmt() {
        return noUsedAmt;
    }

    public void setNoUsedAmt(Double noUsedAmt) {
        this.noUsedAmt = noUsedAmt;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCfDirection() {
        return cfDirection;
    }

    public void setCfDirection(String cfDirection) {
        this.cfDirection = cfDirection;
    }

    public Long getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(Long withdrawId) {
        this.withdrawId = withdrawId;
    }

    public Double getWriteOffAmount() {
        return writeOffAmount;
    }

    public void setWriteOffAmount(Double writeOffAmount) {
        this.writeOffAmount = writeOffAmount;
    }

    public static String getFieldDocumentCategory() {
        return FIELD_DOCUMENT_CATEGORY;
    }

    public Long getProposerUserId() {
        return proposerUserId;
    }

    public void setProposerUserId(Long proposerUserId) {
        this.proposerUserId = proposerUserId;
    }

    public Long getProposerEmployeeAssignId() {
        return proposerEmployeeAssignId;
    }

    public void setProposerEmployeeAssignId(Long proposerEmployeeAssignId) {
        this.proposerEmployeeAssignId = proposerEmployeeAssignId;
    }

    public Long getProposerEmployeeId() {
        return proposerEmployeeId;
    }

    public void setProposerEmployeeId(Long proposerEmployeeId) {
        this.proposerEmployeeId = proposerEmployeeId;
    }

    public static String getFieldDocumentType() {
        return FIELD_DOCUMENT_TYPE;
    }

    public Double getMaxAmt() {
        return maxAmt;
    }

    public void setMaxAmt(Double maxAmt) {
        this.maxAmt = maxAmt;
    }

    public Double getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(Double minAmt) {
        this.minAmt = minAmt;
    }

    public String getBpCode() {
        return bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getBpClass() {
        return bpClass;
    }

    public void setBpClass(String bpClass) {
        this.bpClass = bpClass;
    }

    public static String getFieldCompanyId() {
        return FIELD_COMPANY_ID;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public String getCreditCate() {
        return creditCate;
    }

    public void setCreditCate(String creditCate) {
        this.creditCate = creditCate;
    }

    public Double getOpenToBuy() {
        return openToBuy;
    }

    public void setOpenToBuy(Double openToBuy) {
        this.openToBuy = openToBuy;
    }

    public String getBpCategory() {
        return bpCategory;
    }

    public void setBpCategory(String bpCategory) {
        this.bpCategory = bpCategory;
    }

    @Transient
    private List<HlsCusLonCreditContractAttachment> lonCreditContractAttachmentList;

    public List<HlsCusLonCreditContractAttachment> getLonCreditContractAttachmentList() {
        return lonCreditContractAttachmentList;
    }

    public void setLonCreditContractAttachmentList(List<HlsCusLonCreditContractAttachment> lonCreditContractAttachmentList) {
        this.lonCreditContractAttachmentList = lonCreditContractAttachmentList;
    }

    public String getDocumentType2() {
        return documentType2;
    }

    public void setDocumentType2(String documentType2) {
        this.documentType2 = documentType2;
    }

    public Double getCreditLineAmt() {
        return creditLineAmt;
    }

    public void setCreditLineAmt(Double creditLineAmt) {
        this.creditLineAmt = creditLineAmt;
    }

    public Double getCreditExposureAmt() {
        return creditExposureAmt;
    }

    public void setCreditExposureAmt(Double creditExposureAmt) {
        this.creditExposureAmt = creditExposureAmt;
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

    public void setCreditContractNumber(String creditContractNumber) {
        this.creditContractNumber = creditContractNumber;
    }

    public String getCreditContractNumber() {
        return creditContractNumber;
    }

    public void setCreditContractName(String creditContractName) {
        this.creditContractName = creditContractName;
    }

    public String getCreditContractName() {
        return creditContractName;
    }

    public void setCreditContractStatus(String creditContractStatus) {
        this.creditContractStatus = creditContractStatus;
    }

    public String getCreditContractStatus() {
        return creditContractStatus;
    }

    public void setCreditLineId(Long creditLineId) {
        this.creditLineId = creditLineId;
    }

    public Long getCreditLineId() {
        return creditLineId;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCreditBpId(Long creditBpId) {
        this.creditBpId = creditBpId;
    }

    public Long getCreditBpId() {
        return creditBpId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getExtraNam() {
        return extraNam;
    }

    public void setExtraNam(String extraNam) {
        this.extraNam = extraNam;
    }

    public void setLonCompanyId(Long lonCompanyId) {
        this.lonCompanyId = lonCompanyId;
    }

    public Long getLonCompanyId() {
        return lonCompanyId;
    }

    public String getFinancingChannel() {
        return financingChannel;
    }

    public void setFinancingChannel(String financingChannel) {
        this.financingChannel = financingChannel;
    }

    public String getCreditConNumber() {
        return creditConNumber;
    }

    public void setCreditConNumber(String creditConNumber) {
        this.creditConNumber = creditConNumber;
    }

    public String getCreditReplyNo() {
        return creditReplyNo;
    }

    public void setCreditReplyNo(String creditReplyNo) {
        this.creditReplyNo = creditReplyNo;
    }

    public String getCreditCategory() {
        return creditCategory;
    }

    public void setCreditCategory(String creditCategory) {
        this.creditCategory = creditCategory;
    }

    public Date getDelayFrom() {
        return delayFrom;
    }

    public void setDelayFrom(Date delayFrom) {
        this.delayFrom = delayFrom;
    }

    public Date getDelayTo() {
        return delayTo;
    }

    public void setDelayTo(Date delayTo) {
        this.delayTo = delayTo;
    }

    public Date getFirstWithdrawDate() {
        return firstWithdrawDate;
    }

    public void setFirstWithdrawDate(Date firstWithdrawDate) {
        this.firstWithdrawDate = firstWithdrawDate;
    }

    public String getChannelBusinessFlag() {
        return channelBusinessFlag;
    }

    public void setChannelBusinessFlag(String channelBusinessFlag) {
        this.channelBusinessFlag = channelBusinessFlag;
    }

    public void setRefCreditBpId(Long refCreditBpId) {
        this.refCreditBpId = refCreditBpId;
    }

    public Long getRefCreditBpId() {
        return refCreditBpId;
    }

    public String getRefCreditBpName() {
        return refCreditBpName;
    }

    public void setRefCreditBpName(String refCreditBpName) {
        this.refCreditBpName = refCreditBpName;
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

    public Double getExposureRestAmt() {
        return exposureRestAmt;
    }

    public void setExposureRestAmt(Double exposureRestAmt) {
        this.exposureRestAmt = exposureRestAmt;
    }

    public Double getExposureUsedAmt() {
        return exposureUsedAmt;
    }

    public void setExposureUsedAmt(Double exposureUsedAmt) {
        this.exposureUsedAmt = exposureUsedAmt;
    }

    public Double getLowRiskRestAmt() {
        return lowRiskRestAmt;
    }

    public void setLowRiskRestAmt(Double lowRiskRestAmt) {
        this.lowRiskRestAmt = lowRiskRestAmt;
    }

    public Double getLowRiskUsedAmt() {
        return lowRiskUsedAmt;
    }

    public void setLowRiskUsedAmt(Double lowRiskUsedAmt) {
        this.lowRiskUsedAmt = lowRiskUsedAmt;
    }

    public String getDataClass() {
        return dataClass;
    }

    public void setDataClass(String dataClass) {
        this.dataClass = dataClass;
    }

    public String getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(String confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public void setLastChangeReqId(Long lastChangeReqId) {
        this.lastChangeReqId = lastChangeReqId;
    }

    public Long getLastChangeReqId() {
        return lastChangeReqId;
    }

    public int getExposureCredit() {
        return exposureCredit;
    }

    public void setExposureCredit(int exposureCredit) {
        this.exposureCredit = exposureCredit;
    }

    public int getLowRiskCredit() {
        return lowRiskCredit;
    }

    public void setLowRiskCredit(int lowRiskCredit) {
        this.lowRiskCredit = lowRiskCredit;
    }

    public String[] getCreditCategoryStatus() {
        return creditCategoryStatus;
    }

    public void setCreditCategoryStatus(String[] creditCategoryStatus) {
        this.creditCategoryStatus = creditCategoryStatus;
    }

    public String getCreditCategorys() {
        return creditCategorys;
    }

    public void setCreditCategorys(String creditCategorys) {
        this.creditCategorys = creditCategorys;
    }

    public String getCreditCategoryDesc() {
        return creditCategoryDesc;
    }

    public void setCreditCategoryDesc(String creditCategoryDesc) {
        this.creditCategoryDesc = creditCategoryDesc;
    }

    public String getValidFromQuery() {
        return validFromQuery;
    }

    public void setValidFromQuery(String validFromQuery) {
        this.validFromQuery = validFromQuery;
    }

    public String getValidToQuery() {
        return validToQuery;
    }

    public void setValidToQuery(String validToQuery) {
        this.validToQuery = validToQuery;
    }

    public String getCompetentDept() {
        return competentDept;
    }

    public void setCompetentDept(String competentDept) {
        this.competentDept = competentDept;
    }

    public String getFinanceType() {
        return financeType;
    }

    public void setFinanceType(String financeType) {
        this.financeType = financeType;
    }

    public String getDocumentTypeDesc() {
        return documentTypeDesc;
    }

    public void setDocumentTypeDesc(String documentTypeDesc) {
        this.documentTypeDesc = documentTypeDesc;
    }

    public String getLonCompanyIdDesc() {
        return lonCompanyIdDesc;
    }

    public void setLonCompanyIdDesc(String lonCompanyIdDesc) {
        this.lonCompanyIdDesc = lonCompanyIdDesc;
    }

    public Long getCreditYear() {
        return creditYear;
    }

    public void setCreditYear(Long creditYear) {
        this.creditYear = creditYear;
    }

    public Long getCreditMonth() {
        return creditMonth;
    }

    public void setCreditMonth(Long creditMonth) {
        this.creditMonth = creditMonth;
    }

    public Long getCreditDay() {
        return creditDay;
    }

    public void setCreditDay(Long creditDay) {
        this.creditDay = creditDay;
    }

    public Double getCreditLineAmtLeft() {
        return creditLineAmtLeft;
    }

    public void setCreditLineAmtLeft(Double creditLineAmtLeft) {
        this.creditLineAmtLeft = creditLineAmtLeft;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(String loanTerm) {
        this.loanTerm = loanTerm;
    }

    public String getWithdrawNumber() {
        return withdrawNumber;
    }

    public void setWithdrawNumber(String withdrawNumber) {
        this.withdrawNumber = withdrawNumber;
    }

    public Double getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(Double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public Double getFinanceAmount() {
        return financeAmount;
    }

    public void setFinanceAmount(Double financeAmount) {
        this.financeAmount = financeAmount;
    }

    public Double getParInterestRate() {
        return parInterestRate;
    }

    public void setParInterestRate(Double parInterestRate) {
        this.parInterestRate = parInterestRate;
    }

    public Double getChargeFeeRate() {
        return chargeFeeRate;
    }

    public void setChargeFeeRate(Double chargeFeeRate) {
        this.chargeFeeRate = chargeFeeRate;
    }

    public Double getConsultingFeeRate() {
        return consultingFeeRate;
    }

    public void setConsultingFeeRate(Double consultingFeeRate) {
        this.consultingFeeRate = consultingFeeRate;
    }

    public Double getDepositRate() {
        return depositRate;
    }

    public void setDepositRate(Double depositRate) {
        this.depositRate = depositRate;
    }

    public Double getConsignmentSalesFeeRate() {
        return consignmentSalesFeeRate;
    }

    public void setConsignmentSalesFeeRate(Double consignmentSalesFeeRate) {
        this.consignmentSalesFeeRate = consignmentSalesFeeRate;
    }

    public Double getCollocationFeeRate() {
        return collocationFeeRate;
    }

    public void setCollocationFeeRate(Double collocationFeeRate) {
        this.collocationFeeRate = collocationFeeRate;
    }

    public Double getManagementFeeRate() {
        return managementFeeRate;
    }

    public void setManagementFeeRate(Double managementFeeRate) {
        this.managementFeeRate = managementFeeRate;
    }

    public Double getOtherFeeRate() {
        return otherFeeRate;
    }

    public void setOtherFeeRate(Double otherFeeRate) {
        this.otherFeeRate = otherFeeRate;
    }

    public String getGuaranteeMethod() {
        return guaranteeMethod;
    }

    public void setGuaranteeMethod(String guaranteeMethod) {
        this.guaranteeMethod = guaranteeMethod;
    }

    public String getFinancePrj() {
        return financePrj;
    }

    public void setFinancePrj(String financePrj) {
        this.financePrj = financePrj;
    }

    @Transient
    private Double amtFrom;

    @Transient
    private Double amtTo;

    @Transient
    private Double minCreditLineAmt;

    @Transient
    private Double maxCreditLineAmt;

    @Transient
    private Double minFinanceAmount;

    @Transient
    private Double maxFinanceAmount;

    @Transient
    private Double maxDueAmount;

    @Transient
    private Double minDueAmount;

    public Double getAmtFrom() {
        return amtFrom;
    }

    public void setAmtFrom(Double amtFrom) {
        this.amtFrom = amtFrom;
    }

    public Double getAmtTo() {
        return amtTo;
    }

    public void setAmtTo(Double amtTo) {
        this.amtTo = amtTo;
    }

    public Double getMinCreditLineAmt() {
        return minCreditLineAmt;
    }

    public void setMinCreditLineAmt(Double minCreditLineAmt) {
        this.minCreditLineAmt = minCreditLineAmt;
    }

    public Double getMaxCreditLineAmt() {
        return maxCreditLineAmt;
    }

    public void setMaxCreditLineAmt(Double maxCreditLineAmt) {
        this.maxCreditLineAmt = maxCreditLineAmt;
    }

    public Double getMinFinanceAmount() {
        return minFinanceAmount;
    }

    public void setMinFinanceAmount(Double minFinanceAmount) {
        this.minFinanceAmount = minFinanceAmount;
    }

    public Double getMaxFinanceAmount() {
        return maxFinanceAmount;
    }

    public void setMaxFinanceAmount(Double maxFinanceAmount) {
        this.maxFinanceAmount = maxFinanceAmount;
    }

    public Double getMaxDueAmount() {
        return maxDueAmount;
    }

    public void setMaxDueAmount(Double maxDueAmount) {
        this.maxDueAmount = maxDueAmount;
    }

    public Double getMinDueAmount() {
        return minDueAmount;
    }

    public void setMinDueAmount(Double minDueAmount) {
        this.minDueAmount = minDueAmount;
    }

    public List<HlsCusCreditContractLine> getCreditContractLineList() {
        return creditContractLineList;
    }

    public void setCreditContractLineList(List<HlsCusCreditContractLine> creditContractLineList) {
        this.creditContractLineList = creditContractLineList;
    }

    public String getWithdrawPerequisite() {
        return withdrawPerequisite;
    }

    public void setWithdrawPerequisite(String withdrawPerequisite) {
        this.withdrawPerequisite = withdrawPerequisite;
    }

    public String getPostLoanManage() {
        return postLoanManage;
    }

    public void setPostLoanManage(String postLoanManage) {
        this.postLoanManage = postLoanManage;
    }

    public String getCreationName() {
        return creationName;
    }

    public void setCreationName(String creationName) {
        this.creationName = creationName;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public Long getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    @Override
    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getWithdrawDeadline() {
        return withdrawDeadline;
    }

    public void setWithdrawDeadline(Date withdrawDeadline) {
        this.withdrawDeadline = withdrawDeadline;
    }

    public Long getWithdrawCount() {
        return withdrawCount;
    }

    public void setWithdrawCount(Long withdrawCount) {
        this.withdrawCount = withdrawCount;
    }

    public Double getWithdrawBalanceSum() {
        return withdrawBalanceSum;
    }

    public void setWithdrawBalanceSum(Double withdrawBalanceSum) {
        this.withdrawBalanceSum = withdrawBalanceSum;
    }

    public Double getCreditUnexposureAmt() {
        return creditUnexposureAmt;
    }

    public void setCreditUnexposureAmt(Double creditUnexposureAmt) {
        this.creditUnexposureAmt = creditUnexposureAmt;
    }

    public Long getCreditTypeCount() {
        return creditTypeCount;
    }

    public void setCreditTypeCount(Long creditTypeCount) {
        this.creditTypeCount = creditTypeCount;
    }

    public Double getCreditAvailableAmtSum() {
        return creditAvailableAmtSum;
    }

    public void setCreditAvailableAmtSum(Double creditAvailableAmtSum) {
        this.creditAvailableAmtSum = creditAvailableAmtSum;
    }

    public String getCancelFlag() {
        return cancelFlag;
    }

    public void setCancelFlag(String cancelFlag) {
        this.cancelFlag = cancelFlag;
    }

    public Double getWithdrawBalance() {
        return withdrawBalance;
    }

    public void setWithdrawBalance(Double withdrawBalance) {
        this.withdrawBalance = withdrawBalance;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getReleComFlag() {
        return releComFlag;
    }

    public void setReleComFlag(String releComFlag) {
        this.releComFlag = releComFlag;
    }

    public Long getCancelCount() {
        return cancelCount;
    }

    public void setCancelCount(Long cancelCount) {
        this.cancelCount = cancelCount;
    }

    public String getValidFromFormat() {
        return validFromFormat;
    }

    public void setValidFromFormat(String validFromFormat) {
        this.validFromFormat = validFromFormat;
    }

    public String getValidToFormat() {
        return validToFormat;
    }

    public void setValidToFormat(String validToFormat) {
        this.validToFormat = validToFormat;
    }

    public Double getActualAvailableAmtSum() {
        return actualAvailableAmtSum;
    }

    public void setActualAvailableAmtSum(Double actualAvailableAmtSum) {
        this.actualAvailableAmtSum = actualAvailableAmtSum;
    }

    public String getIsMix() {
        return isMix;
    }

    public void setIsMix(String isMix) {
        this.isMix = isMix;
    }

    public Double getTotalWithdrawAmountSum() {
        return totalWithdrawAmountSum;
    }

    public void setTotalWithdrawAmountSum(Double totalWithdrawAmountSum) {
        this.totalWithdrawAmountSum = totalWithdrawAmountSum;
    }

    public Double getLimitWithdrawAmountSum() {
        return limitWithdrawAmountSum;
    }

    public void setLimitWithdrawAmountSum(Double limitWithdrawAmountSum) {
        this.limitWithdrawAmountSum = limitWithdrawAmountSum;
    }


    @Transient
    private String lonCompanyIdN;

    public String getLonCompanyIdN() {
        return lonCompanyIdN;
    }

    public void setLonCompanyIdN(String lonCompanyIdN) {
        this.lonCompanyIdN = lonCompanyIdN;
    }

    public String getCurrencyN() {
        return currencyN;
    }

    public void setCurrencyN(String currencyN) {
        this.currencyN = currencyN;
    }


    @Transient
    private String creditBpIdN;
    @Transient
    private String releComFlagN;
    @Transient
    private String orgTypeN;

    public String getCreditBpIdN() {
        return creditBpIdN;
    }

    public void setCreditBpIdN(String creditBpIdN) {
        this.creditBpIdN = creditBpIdN;
    }

    public String getReleComFlagN() {
        return releComFlagN;
    }

    public void setReleComFlagN(String releComFlagN) {
        this.releComFlagN = releComFlagN;
    }

    public String getOrgTypeN() {
        return orgTypeN;
    }

    public void setOrgTypeN(String orgTypeN) {
        this.orgTypeN = orgTypeN;
    }


    @Transient
    private String creditAble;

    @Transient
    private String creditUnable;

    public String getCreditAble() {
        return creditAble;
    }

    public void setCreditAble(String creditAble) {
        this.creditAble = creditAble;
    }

    public String getCreditUnable() {
        return creditUnable;
    }

    public void setCreditUnable(String creditUnable) {
        this.creditUnable = creditUnable;
    }

}
