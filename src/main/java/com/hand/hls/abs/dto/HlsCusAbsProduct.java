package com.hand.hls.abs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ExtensionAttribute(disable = true)
@Table(name = "ct_abs_product")
public class HlsCusAbsProduct extends BaseDTO {

    @Id
    @GeneratedValue
    private Long productId; //pk

    @NotNull
    private Long projectId; //立项id

    @NotNull
    private Long companyId; //主体

    @Length(max = 100)
    private String productNumber; //产品编号

    @Length(max = 255)
    private String productName; //产品名称

    @Length(max = 20)
    private String productStatus; //产品状态

    private BigDecimal issueAmount; //发行金额

    private Date productDate; //申请日期

    private Date dueDateBegin; //起息日

    private Date dueDateEnd; //到期日

    private Date rentalBackDate; //租金回收计算日

    @Transient
    private Date baseDate; //基准日

    private Long userId; //主办id

    private Long unitId; //主办部门

    @Length(max = 20)
    private String productLevel; //产品级别

    @Length(max = 2000)
    private String description; //产品概要

    private Double weightedAverageCost; //优先级加权平均成本

    private Date setUpDate; //交易成立日

    private Long deadlineDays; //期限天数

    private Double interestRate; //利率

    @Length(max = 20)
    private String rateRule; //计息规则

    @Length(max = 100)
    private String payInterestFrequency; //付息频次

    @Length(max = 10)
    private String payCapitalFrequency; //还本频次

    private Long interestStandardDays; //利息基准天数

    private String isIrregular; //是否规则

    @Length(max = 20)
    private String cashPeriod; //兑付周期

    private Date firstCashDate; //第一个兑付日

    private Long cashDateT; //兑付日

    private Date firstRentalBackDate;  // 第一个租金回收日

    private Long rentalBackDateT; //租金回收计算日(s)

    private Long reportDateRule; //报告日规则

    private Long collectionDateRule; //归集日规则

    private Long remittanceDateRule; //转付日规则

    @Length(max = 100)
    private String businessType;  // 业务类型

    @Length(max = 100)
    private String documentCategory;  // 单据类别

    @Length(max = 100)
    private String documentType;  // 单据类型

    private Date finishDate;   // 完结日期

    private String finishDescription;  // 完结说明

    @Length(max = 20)
    private String dataClass;   // 单据状态

    private Long refProductId;  // 原产品id

    private Long changeReqId;  // 变更id

    private Double changeRate;  // 变更利率

    private BigDecimal changeAmount;  // 变更金额

    private BigDecimal makeupAmount;  // 总补差金额

    private String changeInfo;  // 变更说明

    private String assetChangeType;  // 资产变更类型

    private String collectionStatus;  // 归集状态

    private String remittanceStatus;  // 转付状态

    private String ownerStatus;   // 自持认购状态


    private String buybackStatus;  //回购状态


    private Long packId;

    /**
     * 项目简称
     */
    private String  productShortName;


    /**
     * 交易场所
     */
    private String  tradingPalce;


    /**
     * 发行方式
     */
    private String  publishMethod;


    /**
     * 发行函编号
     */
    private String  publishNumber;


    /**
     * 发行函名称
     */
    private String publishName;


    /**
     * 产额补足(内部增信方式)
     */
    private String inBalanceComplementFlag;


    /**
     * 产额补足(外部增信方式)
     */
    private String outBalanceComplementFlag;


    /**
     * 回购(内部增信方式)
     */
    private String inBuyBackFlag;


    /**
     * 回购(内部增信方式)
     */
    private String outBuyBackFlag;


    /**
     * 其他(内部增信方式)
     */
    private String inOtherFlag;


    /**
     * 其他(外部增信方式)
     */
    private String outOtherFlag;



    /**
     * 无(内部增信方式)
     */
    private String inNothingFlag;



    /**
     * 无(外部增信方式)
     */
    private String outNothingFlag;


    /**
     * 其他内部增信方式
     */
    private String inOtherDesc;



    /**
     * 其他外部增信方式
     */
    private String outOtherDesc;


    /**
     * 币种
     */
    private String productCurrencyCode;


    /**
     * 计划到期日
     */
    private Date planDateEnd;


    /**
     * 期限（月）
     */
    private Double productTerm;


    /**
     * 计息方式
     */
    private String rateMethod;


    /**
     * 偿付方式
     */
    private String payMethod;


    /**
     *资产是否出表
     */
    private String tableFlag;


    /**
     * 出表日期
     */
    private Date tableDate;


    /**
     * 报表名称
     */
    private String reportName;

    /**
     * 债券代码
     */
    private String bondCode;

    /**
     * 资金投向
     */
    private String fundInvest;


    /**
     * 资金计划行ID
     */
    private Long financePlanLineId;


    /**
     * 主体评级
     */
    private String subjectGrade;

    /**
     * 是否自持
     */
    private String subscribeFlag;


    /**
     * 债券简称
     */
    private String bondShortName;
    /**
     * 发行日
     */
    @Transient
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date publishDate;

    @Transient
    private String companyName;   // 公司名称

    @Transient
    private String createPerson;   // 申请人

    @Transient
    private String unitName;   // 申请部门

    @Transient
    private String condition;  // 查询条件

    @Transient
    private String changeStatus;  // 变更状态

    @Transient
    private Date changeReqDate;   // 变更日期

    @Transient
    private String changeDescription;  // 变更原因

    @Transient
    private Long times; // 期数

    @Transient
    private String[] statusArr;  // 变更状态数组

    @Transient
    private String dateStr;  // 日期范围

    @Transient
    private String projectName;  // 项目名称

    @Transient
    private String projectNumber;  // 项目编号

    @Transient
    private String projectShortName;  //项目简称

    @Transient
    private Long remittanceTimes;   // 已转付期数

    @Transient
    private String[] businessTypes;  // 项目类型

    /**
     * 报价Id
     */
    @Transient
    private Long quotationId;

    /**
     * 测算类型
     */
    @Transient
    private String  measureType;

    /**
     * 提款起始日
     */
    @Transient
    private Date  dueDate;

    /**
     * 计划到期日
     */
    @Transient
    private Date  withdrawEndDate;

    /**
     * 业务期限
     */
    @Transient
    private Double loanTerm;

    /**
     * 提款金额
     */
    @Transient
    private Double  dueAmount;

    /**
     * 利息总金额
     */
    @Transient
    private Double  interestSum;

    /**
     * 汇率
     */
    @Transient
    private Double  exchangeRate;

    /**
     * 折算币种
     */
    @Transient
    private String  convertCurrency;

    /**
     * 折算期初本金金额
     */
    @Transient
    private Double  convertBeginPrincipalAmount;

    /**
     * 折算应付本金总金额
     */
    @Transient
    private Double  convertPayPrincipalAmount;

    /**
     * 折算利息总金额
     */
    @Transient
    private Double  convertPayInterestAmount;

    /**
     * 利率类型
     */
    @Transient
    private String  intRateType;

    /**
     * 基础利率类型
     */
    @Transient
    private String  baseRateType;

    /**
     * 基准利率/拆解利率
     */
    @Transient
    private Double baseRate;

    /**
     * 浮动方式
     */
    @Transient
    private String floatingWay;

    /**
     * 幅度
     */
    @Transient
    private Double floatingWayRange;

    /**
     * 当前利率
     */
    @Transient
    private Double intRate;

    /**
     * 初始提款利率
     */
    @Transient
    private Double firstRate;

    /**
     * 年度计息天数
     */
    @Transient
    private String calcInterestYearDays;

    /**
     * 还款方式
     */
    @Transient
    private String interestCalcMethod;

    /**
     * 结息周期
     */
    @Transient
    private String interestCycle;

    /**
     * 结息月
     */
    @Transient
    private String interestMonth;

    /**
     * 付息日
     */
    @Transient
    private Long interestPaymentDate;

    /**
     * 结息周期还息期数
     */
    @Transient
    private Long loanTimes;

    /**
     * 结息日
     */
    @Transient
    private Long interestCalcDate;

    /**
     * 概算融资成本
     */
    @Transient
    private Double comperhensiveFinancingCost;

    /**
     * XIRR
     */
    @Transient
    private Double xirr;

    /**
     * 税率
     */
    @Transient
    private Double taxRate;

    /**
     * 调息规则
     */
    @Transient
    private String floatingRangeMethod;

    /**
     * 调息规则备注
     */
    @Transient
    private String floatingRangeMethodRemark;

    @Transient
    private Date rateChangeDate;

    @Transient
    private Double rateChangeAfter;

    @Transient
    private String calcInterestYearDaysAfter;

    @Transient
    private String  productBusinessType;

    @Transient
    private String  packNumber;

    /**
     * 未收租金
     */
    @Transient
    private Double uncollectedDueAmount;

    /**
     * 未收本金
     */
    @Transient
    private Double uncollectedPrincipal;

    /**
     * 未收利息
     */
    @Transient
    private Double uncollectedInterest;

    /**
     * 应收租金
     */
    @Transient
    private Double collectedDueAmount;

    /**
     * 应收本金
     */
    @Transient
    private Double collectedPrincipal;

    /**
     * 应收利息
     */
    @Transient
    private Double collectedInterest;


    /**
     * 入池资产合同个数
     */
    @Transient
    private Long assetContractCount;


    /**
     * 入池资产承租人个数
     */
    @Transient
    private Long assetRentCount;

    /**
     * 项目类型英文简称
     */
    @Transient
    private String projectEnglishType;
    /**
     * 分摊总金额
     */
    @Transient
    private Double productIncomeAmount;
    @Transient
    private Double incomeOtherFee;
    @Transient
    private Long cfItem;


    @Transient
    private Double surplusAmount;


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
    private String baseAssetType;


    @Transient
    private String otherProjectType;


    @Transient
    private Date packageEndDate;
    @Transient
    private Double issueSurplusAmount;

    @Transient
    private String productStatusN;

    @Transient
    private String baseAssetTypeN;

    @Transient
    private String packIdN;


    @Transient
    private String tradingPalceN;
    @Transient
    private String publishMethodN;
    @Transient
    private String tableFlagN;
    @Transient
    private String subjectGradeN;
    @Transient
    private String subscribeFlagN;

    @Transient
    private String interestStandardDaysN;
    @Transient
    private String payCapitalFrequencyN;
    @Transient
    private String payInterestFrequencyN;
    @Transient
    private String payMethodN;
    @Transient
    private String productCurrencyCodeN;
    @Transient
    private String rateMethodN;
    @Transient
    private String cashPeriodN;
    @Transient
    private String publishNumberN;
    @Transient
    private String financePlanLineIdN;


    public static final String FIELD_PRODUCT_ID = "productId";

    private String ifRelatePack;
    @Transient
    private String ifRelatePackN;
    @Transient
    private Date baseDateFrom;
    @Transient
    private Date baseDateTo;
    @Transient
    private Double collectedOtherAmount;
    @Transient
    private String subscribeMainName;
    @Transient
    private String subscribeAccountName;
    @Transient
    private String subscribeAccountNum;
    @Transient
    private String subscribeBranchName;

}


