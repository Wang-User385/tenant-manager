package com.hand.hls.fin.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@ExtensionAttribute(disable = true)
@Table(name = "lon_contract_withdraw")
@Setter
@Getter
public class HlsCusLonContractWithdraw extends LonContractWithdraw {
    public static final String DOCUMENT_TYPE = "STD";
    public static final String DOCUMENT_CATEGORY = "LON_CONTRACT_WITHDRAW";
    public static final String BUSINESS_TYPE = "LON_CONTRACT_WITHDRAW";
    public static final String FIELD_WITHDRAW_PLAN_ID = "withdrawPlanId";
    public static final String FIELD_TIMES = "times";
    public static final String FIELD_INT_RATE = "intRate";
    public static final String FIELD_CHARGE_FEE = "chargeFee";
    public static final String FIELD_CONSULTING_FEE = "consultingFee";
    public static final String FIELD_OTHER_FEE = "otherFee";
    public static final String FIELD_CONSIGNMENT_SALES_FEE = "consignmentSalesFee";
    public static final String FIELD_COLLOCATION_FEE = "collocationFee";
    public static final String FIELD_MANAGEMENT_FEE = "managementFee";
    public static final String FIELD_DEPOSIT = "deposit";
    public static final String FIELD_INT_RATE_TYPE = "intRateType";
    public static final String FIELD_BASE_RATE_TYPE = "baseRateType";
    public static final String FIELD_BASE_RATE = "baseRate";
    public static final String FIELD_FLOATING_WAY = "floatingWay";
    public static final String FIELD_FLOATING_WAY_RANG = "floatingWayRange";
    public static final String FIELD_FLOATING_RANG_METHOD = "floatingRangeMethod";
    public static final String FIELD_CALC_INTEREST_YEAR_DAYS = "calcInterestYearDays";
    public static final String FIELD_WITHDRAW_END_DATE = "withdrawEndDate";
    public static final String FIELD_DATA_CLASS = "dataClass";
    public static final String FIELD_HISTORY_REASON = "historyReason";
    public static final String FIELD_CHANGE_REQ_ID = "changeReqId";
    public static final String FIELD_CHARGE_FEE_RATE = "chargeFeeRate";
    public static final String FIELD_CONSULTING_FEE_RATE = "consultingFeeRate";
    public static final String FIELD_DEPOSIT_RATE = "depositRate";
    public static final String FIELD_CONSIGNMENT_SALES_FEE_RATE = "consignmentSalesFeeRate";
    public static final String FIELD_COLLOCATION_FEE_RATE = "collocationFeeRate";
    public static final String FIELD_MANAGEMENT_FEE_RATE = "managementFeeRate";
    public static final String FIELD_CHARGE_FEE_METHOD = "chargeFeeMethod";
    public static final String FIELD_CONSULTING_FEE_METHOD = "consultingFeeMethod";
    public static final String FIELD_DEPOSIT_METHOD = "depositMethod";
    public static final String FIELD_CONSIGNMENT_SALES_FEE_METHOD = "consignmentSalesFeeMethod";
    public static final String FIELD_COLLOCATION_FEE_METHOD = "collocationFeeMethod";
    public static final String FIELD_MANAGEMENT_FEE_METHOD = "managementFeeMethod";
    public static final String FIELD_OTHER_FEE_RATE = "otherFeeRate";
    public static final String FIELD_OTHER_FEE_METHOD = "otherFeeMethod";
    public static final String FIELD_RATE_CHANGE_DATE = "rateChangeDate";
    public static final String FIELD_RATE_CHANGE_AFTER = "rateChangeAfter";
    public static final String FLOATING_RATE_START_DATE = "floatingRateStartDate";
    private Long withdrawPlanId;
    private Long changeVersionId;
    private String changeStatus;
    private Long times;
    @Transient
    private Long loanTimes;
    @NumberFormat(pattern="#,###,###.00000000000")
    private Double intRate;

    private Double chargeFee;

    private Double consultingFee;

    private Double otherFee;

    private Double consignmentSalesFee;

    private Double collocationFee;

    private Double managementFee;

    private Double deposit;

    private String intRateType;

    private String baseRateType;

    private Double baseRate;

    private String floatingWay;

    private Double floatingWayRange;

    private String floatingRangeMethod;

    private String calcInterestYearDays;

    private Date withdrawEndDate;

    private String dataClass;

    private String historyReason;

    private Long changeReqId;

    private Double chargeFeeRate;

    private Double consultingFeeRate;

    private Double depositRate;

    private Double consignmentSalesFeeRate;

    private Double collocationFeeRate;

    private Double managementFeeRate;

    private String chargeFeeMethod;

    private String consultingFeeMethod;

    private String depositMethod;

    private String consignmentSalesFeeMethod;

    private String collocationFeeMethod;

    private String managementFeeMethod;

    private Double otherFeeRate;//其他费率

    private String otherFeeMethod;//其他费用付款频率

    private Date rateChangeDate;
    private  Date floatingRateStartDate;

    private Double rateChangeAfter;

    private String calcInterestYearDaysAfter;

    /**
     * 贷款科目
     */
    private String  withdrawSubject;

    /**
     * 放款机构
     */
    private Long loanBpId;

    /**
     * 会计分类
     */
    private String accountClass;

    /**
     * 资金投向
     */
    private String fundInvest;

    /**
     * 报表名称
     */
    private String reportName;

    /**
     * 汇率
     */
    private Double exchangeRate;

    /**
     * 审批通过日期
     */
    private Date approvalDate;

    /**
     * 提款类型
     */
    private String withdrawType;

    private Long withBankAccountId;

    /**
     * 融资计划行ID
     */
    private Long financePlanLineId;

    @Transient
    private Double xirr;
    /**
     * 税率
     */
    @Transient
    private Double taxRate;

    private String withBankAccountNum;

    private String withBankAccountName;

    private String withBankBranchName;

    private Date planInterestPaymentDate;

    private Date planPrincipalPaymentDate;

    private Long contractId;

    private Long companyId;

    private String documentType;

    private String documentCategory;

    private String businessType;

    private String withdrawNumber;

    private String withdrawName;

    private String withdrawStatus;
    private Long cfItem;
    private Long cfType;

    private String cfDirection;

    private String cfStatus;
    private Date dueDate;

    private String writeOffFlag;
    private Double writeOffAmount;
    private String description;

    private Long groupId;
    private Double withdrawRate;

    @Transient
    private String creditCurrencyCode;

    @Transient
    private String withdrawPerequisite;

    @Transient
    private String postLoanManage;

    @Transient
    private String loanBpName;

    @Transient
    private Double prevDueAmount;

    private Double dueAmount;
    @Transient
    private Double oldWithdrawId;
    @Transient
    private Double oldContractId;
    @Transient
    private String bankAccountName;

    @Transient
    private String bankBranchName;

    @Transient
    private Long bankAccountId;

    @Transient
    private String bankAccountNum;
    @Transient
    private String changeFlag;
    @Transient
    private String changeFlagN;


    /**
     * 放款机构简称
     */
    @Transient
    private String  extraNam;


    /**
     * 提款金额
     */
    @Transient
    private Double cnyDueAmount;


    /**
     * 合同状态
     */
    @Transient
    private String  settleStatus;

    @Transient
    private Double withdrawBalance;
    @Transient
    private Double convertBeginPrincipalAmout;

    @Transient
    private List<HlsCusLonContractRepayment> hlsCusLonContractRepaymentList;

    @Transient
    private List<HlsCusLonContractRepayment> hlsCusLonContractPaymentList;

    @Transient
    private List<HlsCusLonContractPurpose> hlsCusLonContractPurposeList;

    @Transient
    private Long lonCompanyId;

    @Transient
    private String financingChannel;

    @Transient
    private Double financeAmount;

    @Transient
    private String domesticOverseaFinance;

    @Transient
    private Double loanRate;

    @Transient
    private String interestCycle;

    @Transient
    private String interestCalcMethod;

    @Transient
    private String creditConNumber;

    @Transient
    private Long withdrawBankAccountId;

    @Transient
    private String documentTypeDesc;

    @Transient
    private String creditCategory;

    @Transient
    private String documentType2;

    @Transient
    private  String withdrawBankAccountName;

    @Transient
    private String withdrawBankAccountNumber;

    @Transient
    private Double usedWithdrawAmount;

    @Transient
    private Double restWithdrawAmount;

    @Transient
    private String withdrawDateFrom;

    @Transient
    private String withdrawDateTo;

    @Transient
    private String withdrawEndDateFrom;

    @Transient
    private String withdrawEndDateTo;


    @Transient
    private String repaymentIdList;


    @Transient
    private String lonCompanyIdDesc;

    @Transient
    private String withdrawCondition;

    @Transient
    private String withdrawStatusArray;

    @Transient
    private String financingChannelArray;

    @Transient
    private String baseRateDesc;

    @Transient
    private String financingChannelDesc;

    @Transient
    private String changeReqStatus;

    @Transient
    private String purposeList;

    @Transient
    private String bpCode;

    @Transient
    private String bpTypeDesc;

    @Transient
    private Double loanTerm;

    @Transient
    private String loanTermUom;

    @Transient
    private Double partPercent;

    @Transient
    private Double totalAmount;

    @Transient
    private String releCreditFlag;

    @Transient
    private String  majorContractNumber;

    @Transient
    private Double creditLineAmt;

    @Transient
    private Double openToBuy;

    @Transient
    private Double creditExposureAmt;

    @Transient
    private Double minAmt;

    @Transient
    private Double maxAmt;

    @Transient
    private Long minLeaseTerm;

    @Transient
    private Long maxLeaseTerm;

    @Transient
    private Long quotationId;

    /**
     * 初始提款利率
     */
    @Transient
    private Double firstRate;



    /**
     * 是否收取服务费
     */
    @Transient
    private String serveFlag;


    /**
     * 服务费金额
     */
    @Transient
    private Double serveAmount;


    /**
     * 服务费费率
     */
    @Transient
    private Double serveRate;


    /**
     * 服务费收取方式
     */
    @Transient
    private String serveMethod;


    /**
     * 服务费币种
     */
    @Transient
    private String serveCurrencyCode;


    /**
     * 概算融资成本
     */
    @Transient
    private Double comperhensiveFinancingCost;

    @Transient
    private Long interestPaymentDate; //付息日

    @Transient
    private Long interestCalcDate;//结息日

    @Transient
    private String principalCycle;
    @Transient
    private String principalCycleN;

    @Transient
    private Long principalPaymentDate;

    @Transient
    private String quarter;

    @Transient
    private Long seqNum;

    @Transient
    private String interestMonth;

    /**
     * 融资币种
     */
    @Transient
    private String currency;

    @Transient
    private Double interestRateOfReturn;//XIRR

    @Transient
    private  List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachments;

    /**
     * 查询字段
     */
    @Transient
    private String queryObject;

    @Transient
    private String queryMessage;

    @Transient
    private String releComFlag;

    /**
     * 利息汇总
     */
    @Transient
    private Double  interestSum;

    /**
     * 测算类型
     */
    @Transient
    private String measureType;

    /**
     * 折算币种
     */
    @Transient
    private String convertCurrency;

    /**
     * 折算期初本金金额（元） CONVERT_BEGIN_PRINCIPAL_AMOUNT
     */
    @Transient
    private Double convertBeginPrincipalAmount;

    /**
     * 折算应付本金总金额（元） CONVERT_PAY_PRINCIPAL_AMOUNT
     */
    @Transient
    private Double convertPayPrincipalAmount;

    /**
     * 折算应付利息总金额（元）CONVERT_PAY_INTEREST_AMOUNT
     */
    @Transient
    private Double convertPayInterestAmount;

    @Transient
    private String floatingRangeMethodRemark;


    private String changeType;
    private Date changeTime;
    private String changeReason;

    private String interStatus;

    private String interInfo;

    private String withdrawCurrencyCode;

    @Transient
    private Date validFrom;

    @Transient
    private Date validTo;

    /**
     * 融资计划编号
     */
    @Transient
    private String planNumber;

    /**
     * 融资计划名称
     */
    @Transient
    private String planName;

    @Transient
    private Date conditionDate;

    @Transient
    private String timeCondition;

    @Transient
    private Double amountFrom;

    @Transient
    private Double amountTo;


    @Transient
    private String contractName;
    @Transient
    private String contractNumber;
    @Transient
    private Long paidAmount;
    @Transient
    private String bpName;
    @Transient
    private Long rate;

    @Transient
    private String creditBpName;

    @Transient
    private String contractStatus;

    @Transient
    private String withdrawAli;

    @Transient
    private  String  withdrawStatusDesc;

    @Transient
    private String createdByN;
    @Transient
    private String creditBpIdN;
    @Transient
    private Long creditBpId;
    @Transient
    private String creditContractId;
    @Transient
    private String creditContractIdN;
    @Transient
    private String lonCompanyIdN;
    @Transient
    private Double outstandingPrincipal;
    @Transient
    private  String changeVersionIdN;
    @Transient
    private  String changeTypeN;

    private String autoWrite;
    @Transient
    private String autoWriteN;

    @Transient
    private String priceList;
    @Transient
    private String priceListN;

    @Transient
    private String withdrawStatusN;

    @Transient
    private String   documentTypeN;

    @Transient
    private String releCreditFlagN;

    @Transient
    private String measureTypeN;

    @Transient
    private String intRateTypeN;

    @Transient
    private String baseRateTypeN;

    @Transient
    private String floatingWayN;

    @Transient
    private String calcInterestYearDaysN;

    @Transient
    private String interestCalcMethodN;

    @Transient
    private String interestCycleN;

    @Transient
    private String serveFlagN;

    @Transient
    private String serveCurrencyCodeN;

    @Transient
    private String serveMethodN;

    @Transient
    private String floatingRangeMethodN;

    @Transient
    private String fundInvestN;

    @Transient
    private String withBankAccountIdN;

    @Transient
    private Date dueDateFrom;
    @Transient
    private Date dueDateTo;
    private Date initialWithdrawEndDate;
    @Transient
    private String dueDateFromL;
    @Transient
    private String dueDateToL;
    private String stampDutyAccruedAmount;
    private String postFlag;
    @Transient
    private String periodName;
    @Transient
    private Date initialWithdrawEndDateFrom;
    @Transient
    private Date initialWithdrawEndDateTo;

    @Transient
    private List<HlsCusLonContractAttachment> lonContractAttachments;

    @Transient
    private List<HlsCusCtLonContractBankAccount> lonContractBankAccounts;

    @Transient
    private String currencyN;

    @Transient
    private String financePlanLineIdN;

    @Transient
    private String creditContractNumber;

    @Transient
    private String creditBpNameN;

    @Transient
    private String  loanBpIdN ;

    @Transient
    private String creditConNumberN;

    @Transient
    private String creditCategoryN;
    @Transient
    private String conReleaseFlagN;

    @Transient
    private String creditCurrencyCodeN;
    @Transient
    private String releCompanyFlag;
    @Transient
    private String releCompanyFlagN;
    @Transient
    private String documentType2N;
    @Transient
    private String changeStatusN;
    @Transient
    private String interestMonthN;

    @Transient
    private Date baseRateChangeDate;

    @Transient
    private Long changeTerm;

    @Transient
    private Double chargeAmountSum;

    @Transient
    private Double leaseAmountProportion;

    @Transient
    private String contractDocumentType;

    @Transient
    private Long rentingFrequency;
    @Transient
    private String rentingFrequencyN;
}
