package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ExtensionAttribute(disable = true)
@Table(name = "csh_payment_req_ln")
public class HlsCusCshPaymentReqLn extends BaseDTO {
    @Id
    @GeneratedValue
    private Long paymentReqLnId;
    @Condition(
            operator = "LIKE"
    )
    private Long paymentReqId;
    private Long fundingPlanLnId;
    private String sourceDocCategory;
    private Long sourceDocId;
    private Long sourceDocLineId;
    private Double amount;
    private Double amountPaid;
    private String description;
    private String paymentFlag;
    private Date paymentCompletedDate;
    private String paymentMethod;
    private Long bpId;
    private Long bpBankAccountId;
    private String bpBankName;
    private String bpBankBranchName;
    private String bpBankAccountNum;
    private String bpBankAccountName;

    private Double dueAmountLn;
    private String billType;

    private Long seqNum;

    private Double sumDueAmount;
    private String validityLc;
    private Long acceptancePeriod;
    private Long billId;
    @Transient
    private String billIdN;
    @Transient
    private String bankAccountNum;
    @Transient
    private String bankAccountName;
    @Transient
    private Long bankAccountId;
    @Transient
    private String lnIdStr;
    @Transient
    private String cfItem;
    @Transient
    private String cfItemN;
    @Transient
    private String cfType;
    @Transient
    private Double nonPayment;
    @Transient
    private Double remainingPayableAmount;

    private String currency;
    @Transient
    private String paymentObj;
    @Transient
    private String contractId;
    @Transient
    @Condition(
            operator = "LIKE"
    )
    private String contractName;
    @Transient
    @Condition(
            operator = "LIKE"
    )
    private String contractNumber;
    @Transient
    private String proposer;
    @Transient
    private String tenantName;
    @Transient
    private String times;
    @Transient
    private String yfDescription;
    @Transient
    private Double dueAmount;
    @Transient
    private Double unpaidAmount;
    @Transient
    private String dueDate;
    @Transient
    @Condition(
            operator = "LIKE"
    )
    private String bpName;
    @Transient
    private Double startAmount;
    @Transient
    private Double endAmount;
    @Transient
    private Long cashflowId;
    @Transient
    private String paymentReqStatus;
    @Transient
    private String paymentReqStatusDesc;
    @Transient
    private String companyFullName;
    @Transient
    private String typeDescription;
    @Transient
    private String paymentReqNumber;
    @Transient
    private String userName;
    @Transient
    private String paymentStatus;
    @Transient
    private Date paymentReqDate;
    @Transient
    private String createName;
    @Transient
    private Date lastUpdateDate;
    @Transient
    private String documentType;
    @Transient
    private Long companyId;
    @Transient
    private Long processInstanceId;
    @Transient
    private Long queryTimeSolt;
    @Transient
    private Double receivedAmount;
    @Transient
    private String repaymentIds;
    @Transient
    private Long withdrawCfId;
    private String bankCode;

    private Long returnAccount;//回款账户

    private String selfBankAccountId;//我方银行账户id

    private String selfBankName;//我方银行名称

    private String selfBankBranchName;//我方开户支行

    private String selfBankAccountNum;//我方银行账户号

    private String selfBankAccountName;//我方银行账户名

    private String fundSource;//资金来源

    /**
     * 放款日期
     */
    @Transient
    private Date applyPayDate;

    @Transient
    private String paymentMethodDesc;

    @Transient
    private String paymentFlagDesc;

    @Transient
    private String paymentFlagN;

    @Transient
    private String fundSourceDesc;
    @Transient
    private Date rentStartDate;
    @Transient
    private Date rentEndDate;


    private Double deductAmount;

    @Transient
    private String cfDirection;
    @Transient
    private String cfStatus;
    @Transient
    private String planIdN;
    @Transient
    private String bpIdN;
    @Transient
    private String paymentMethodN;
    @Transient
    private String fundingPlanStatus;

    private String ifFromContract;
    @Transient
    private String ifFromContractN;
    @Transient
    private String currencyN;
    @Transient
    private Long projectId;
    @Transient
    private Double exchange;
    @Transient
    private String bpType;
    @Transient
    private String easCode;




    @Transient
    private String iban;
    @Transient
    private String billTypeN;
    @Transient
    private String swift;
    @Transient
    private String beneficiary;





    private Long purchaseContractId;

    private Double purchaseContractAmount;


    private Double purchaseContractAmountSum;

    private Double purchaseContractBalance;
    private Double contractBalanceRelease;

    @Transient

    private Double purchaseContractRelease;
    @Transient
    private  String  bpBankAccountNameN;
    @Transient
    private  String  purchaseContractIdN;

    private String bankAddress;

    private Double currentAppliedAmount;
    @Transient
    private Double paidAmountTotal;
    @Transient
    private String reverseFlag;

    @Transient
    private String bpCode;
    @Transient
    private Double deductionAmount;
    @Transient
    private Double canDeductionAmount;
    @Transient
    private Double surplusAmount;
    @Transient
    private Double cccDueAmount;
    @Transient
    private Double appliedPayAmount;
    @Transient
    private Double residualAmount;
    @Transient
    private Long quotationId;
    @Transient
    private String quotationNumber;

    /**
     * 保证金
     */
    @Transient
    private Double depositDueAmount;

    /**
     * 已收保证金
     */
    @Transient
    private Double depositReceivedAmount;

    /**
     * 服务费
     */
    @Transient
    private Double serviceChargeDueAmount;

    /**
     * 已收服务费
     */
    @Transient
    private Double serviceChargeReceivedAmount;

    /**
     * 厂商服务费(贴息)
     */
    @Transient
    private Double serviceChargeFeeDueAmount;

    /**
     * 已收厂商服务费(贴息)
     */
    @Transient
    private Double serviceChargeFeeReceivedAmount;
    /**
     * 经销商/主机厂
     */
    @Transient
    private String factoryName;
    /**
     * 合作方
     */
    @Transient
    private String manufacturerName;

    @Transient
    private String leaseChannel;

    @Transient
    private String businessType;

    /**
     * 利率(%)
     */
    @Transient
    private Double intRate;
    /**
     * 内部收益率(%)
     */
    @Transient
    private Double xirr;

    @Transient
    private Double irr;
    /**
     * 期限(年)
     */
    @Transient
    private Long leaseTerm;
    /**
     * 资金用途
     */
    @Transient
    private String financeNote;


    public Long getPurchaseContractId() {
        return purchaseContractId;
    }

    public void setPurchaseContractId(Long purchaseContractId) {
        this.purchaseContractId = purchaseContractId;
    }
}
