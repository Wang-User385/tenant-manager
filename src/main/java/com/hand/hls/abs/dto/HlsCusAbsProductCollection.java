package com.hand.hls.abs.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ExtensionAttribute(disable = true)
@Table(name = "ct_abs_product_collection")
public class HlsCusAbsProductCollection extends BaseDTO {
    @Id
    @GeneratedValue
    private Long collectionId; //pk

    @NotNull
    private Long productId; //产品id

    private Long times; //期数

    private Date rentalBackDate;   // 租金回收计算日

    private Date cashDate; //兑付日

    private Date collectionDate; //归集日

    private Date remittanceDate; //转付日

    private Date reportDate; //报告日

    private BigDecimal dueAmount; //应归集金额

    private BigDecimal principal; //应归集本金

    private BigDecimal interest;  //应归集利息

    private String collectionNumber;   // 归集申请编号

    private Date collectionApplyDate;   // 归集申请日期

    private BigDecimal collectionAmount;   // 归集申请金额

    private String collectionStatus;   // 归集申请状态

    private Long collectionOrganizationId;  // 归集机构

    private Long collectionAccountId;  // 基础资产账户

    private Long imputationAccountId;  // 归集账户

    private String remittanceNumber;   // 转付申请编号

    private Date remittanceApplyDate;   // 转付申请日期

    private BigDecimal remittanceAmount;   // 转付申请金额

    private String remittanceStatus;   // 转付申请状态

    private Long remittanceOrganizationId;  // 转付机构

    private String cashNumber;   // 兑付申请编号

    private Date cashApplyDate;   // 兑付申请日期

    private BigDecimal cashAmount;   // 兑付申请金额

    private String cashStatus;   // 兑付申请状态

    private String completeCash;   // 是否兑付完全

    private BigDecimal serviceFee; //服务费

    private BigDecimal hostingFee; //托管费

    private BigDecimal managementFee; //管理费

    private BigDecimal underwritingFee; //承销费

    private BigDecimal ratingFee;//评级费

    private BigDecimal evaluationFee; //评估费

    private BigDecimal listingFee;//挂牌费

    private BigDecimal lawyerFee;//律师费

    private BigDecimal accountantFee;//会计师费

    private BigDecimal otherFee;//其他费用

    private BigDecimal taxFee; //附加税

    private Long interestPeriodDays; //计息天数

    private String  dataClass;

    private Long collectionCfItem;

    private Long collectionCfType;

    private String collectionBankBranchName;

    private String collectionBankAccountName;

    private String collectionBankAccountNum;

    private String collectionOrganizationName;

    private BigDecimal collectionReceiverAmount;

    private Long remittanceCfItem;

    private Long remittanceCfType;

    private Long remittanceAccountId;

    private String remittanceBankBranchName;

    private String remittanceBankAccountName;

    private String remittanceBankAccountNum;

    private String remittanceOrganizationName;

    private BigDecimal remittanceReceiverAmount;

    private String confirmFlag;

    @Transient
    private Date beforeRentalBackDate;   // 上期租金回收计算日

    @Transient
    private Long projectId;  // 项目id

    @Transient
    private String bpName;  // 商业伙伴名称

    @Transient
    private String bankAccountNum;   // 账户

    @Transient
    private String bankAccountName;  // 账户名称

    @Transient
    private String bankName;  // 开户行

    @Transient
    private Long oldCollectionId;

    @Transient
    private String collectionAccountName;   // 基础资产账户

    @Transient
    private String collectionAccountNumber;   // 基础资产账号

    @Transient
    private String imputationAccountNumber; //归集账号

    @Transient
    private String imputationAccountName; //归集账户

    @Transient
    private BigDecimal planCashPrincipalSum;//计划兑付本金

    @Transient
    private BigDecimal planCashInterestSum;//计划兑付利息


    @Transient
    private BigDecimal surplusAmount;//盈余收益


    @Transient
    private BigDecimal cashPrincipalSum;//兑付本金

    @Transient
    private BigDecimal cashInterestSum;//兑付利息

    @Transient
    private String productNumber;

    @Transient
    private String productShortName;

    @Transient
    private String businessType;

    @Transient
    private String productName;
    @Transient
    private Double incomeFee;
    @Transient
    private Long cfItem;
    @Transient
    private List<HlsCusAbsProductCashDetail> hlsCusAbsProductCashDetails;

    @Transient
    private List<HlsCusAbsProductRelease> hlsCusAbsProductReleases;

    @Transient
    private List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachments;

}

