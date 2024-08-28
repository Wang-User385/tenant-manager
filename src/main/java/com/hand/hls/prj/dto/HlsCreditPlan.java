package com.hand.hls.prj.dto;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "HLS_CREDIT_PLAN")
public class HlsCreditPlan {
    /**
     * ID
     */
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY,generator = "select SEQ_ID.nextval from dual")
    @GeneratedValue
    @Column(name = "CREDIT_PLAN_ID")
    private Long creditPlanId;

    /**
     * 授信金额
     */
    @Column(name = "CREDIT_AMT")
    private Double creditAmt;

    /**
     * 额度类型
     */
    @Column(name = "QUOTA_TYPE")
    private String quotaType;

    /**
     * 是否集团授信
     */
    @Column(name = "CONGLOMERATE_FLAG")
    private String conglomerateFlag;

    /**
     * 所属集团
     */
    @Column(name = "BELONG_CONGLOMERATE")
    private String belongConglomerate;

    /**
     * 授信开始日期
     */
    @Column(name = "DATE_FROM")
    private Date dateFrom;

    /**
     * 授信结束日期
     */
    @Column(name = "DATE_TO")
    private Date dateTo;

    /**
     * 授信说明
     */
    @Column(name = "CREDIT_DESC")
    private String creditDesc;

    /**
     * 资金用途
     */
    @Column(name = "FINANCE_NOTE")
    private String financeNote;

    /**
     * 其他
     */
    @Column(name = "OTHER")
    private String other;

    @Column(name = "OBJECT_VERSION_NUMBER")
    private Long objectVersionNumber;

    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "PROGRAM_ID")
    private Long programId;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Column(name = "LAST_UPDATED_BY")
    private Long lastUpdatedBy;

    @Column(name = "LAST_UPDATE_DATE")
    private Date lastUpdateDate;

    @Column(name = "LAST_UPDATE_LOGIN")
    private BigDecimal lastUpdateLogin;

    /**
     * 授信单据类别, 立项/尽调/授信管理 
     */
    @Column(name = "SOURCE_DOCUMENT_CATEGORY")
    private String sourceDocumentCategory;

    @Column(name = "SOURCE_DOCUMENT_ID")
    private Long sourceDocumentId;

    /**
     *  授信变更前的授信金额
     */
    @Column(name = "CHANGE_BEFORE_CREDIT_AMT")
    private Double changeBeforeCreditAmt;

    /**
     * 授信变更前的授信结束日期
     */
    @Column(name = "DATE_TO_BEFORE")
    private Date dateToBefore;
    private  Double buyBackDay;
    private  Double compenDay;
    private  Double penaltyRate;
    private  Double leaseChargeRatio;
    private  String depositColMethod;
    private  Double intRate;

    @Transient
    private Long chanceId;

    @Transient
    private String quotaTypeN;

    @Transient
    private String conglomerateFlagN;
    /**
     * 内部收益率
     */
    private Double irr;

    /**
     * 融资期限（年）
     */
    private Long financingDate;

    @Transient
    private Double leftCreditAmt;

    @Transient
    private String changeDesc;

    private Double nonCyclicalAmount;
    private Double cyclicalAmount;

    /**
     * 调息方式
     */
    private String floatingRangeMethod;
    @Transient
    private String floatingRangeMethodN;
    /**
     * 厂商担保方式
     */
    private String guaranteeMethod;
    @Transient
    private String guaranteeMethodN;
    /**
     * 经销商是否承担担保
     */
    private String bearGuaranteeFlag;
    @Transient
    private String bearGuaranteeFlagN;

    @Transient
    private String bpName;
    @Transient
    private Double usedAmount;

    private String guaranteeMethodRent;
    @Transient
    private String guaranteeMethodRentN;
    private String guaranteeMethodDifference;
    @Transient
    private String guaranteeMethodDifferenceN;
    private String guaranteeMethodRepurcharse;
    @Transient
    private String guaranteeMethodRepurcharseN;
    private String guaranteeMethodResponse;
    @Transient
    private String guaranteeMethodResponseN;

    private Double downPaymentRatio;
    private Double depositRatio;
    private String annualPayTimes;
    @Transient
    private String annualPayTimesN;
    private String residualPriceType;
    @Transient
    private String residualPriceTypeN;
    private Double residual;
}