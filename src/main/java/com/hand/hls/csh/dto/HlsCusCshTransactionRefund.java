package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/4/24
 * @description:
 */
@Getter
@Setter
@ExtensionAttribute(disable = true)
@Table(name = "csh_transaction_refund")
public class HlsCusCshTransactionRefund extends CshTransactionRefund{
    @Transient
    private String currencyName;
    @Transient
    private String refundStatusDesc;

    private String bpBankAccountNum; //收款银行账号

    private String bpBankAccountName; //收款账户名
    @Transient
    private String bankAccountNum;
    @Transient
    private String bankAccountName;
    @Transient
    private String createdByName;
    @Transient
    private String creationDateFormat;
    @Transient
    private String bpName;
    @Transient
    private String bpPaymentName;
    /**
     * 付款对象类别
     */
    @Transient
    private String bpNameCategory;
    /**
     * 二期功能：退款支付方式
     */
    private String paymentMethod;
    @Transient
    private String paymentMethodN;

    private String bpBankName; //收款银行

    private String bpBankBranchName; //收款银行支行名称

    @Transient
    private String transactionTypeN;
    @Transient
    private String transactionType;
    @Transient
    private String contractNumber;
    @Transient
    private String contractName;
    @Transient
    private Long sumDueAmount;
    @Transient
    private Long surplusDueAmount;
    @Transient
    private String paymentRefundStatusN;
    @Transient
    private Long companySpv;
    @Transient
    private String companySpvN;
    @Transient
    private Date transactionDate;
    @Transient
    private Date transactionDateFrom;

    @Transient
    private Date transactionDateTo;

    @Transient
    private Double transactionAmount;
    @Transient
    private Double transactionAmountFrom;

    @Transient
    private Double transactionAmountTo;

    @Transient
    private String manufacturerIdN;

    @Transient
    private Long bpIdVender;

    @Transient
    private String bpIdVenderN;

    @Transient
    private Long factoryId;

    @Transient
    private String factoryIdN;

    @Transient
    private String isTerminate;

    @Transient
    private String isTerminateN;

    @Transient
    private Date refundDateFrom;

    @Transient
    private Date refundDateTo;

    @Transient
    private Double refundAmountFrom;

    @Transient
    private Double refundAmountTo;

    @Transient
    private String transactionNum;
    @Transient
    private Long manufacturerId;
    @Transient
    private String transactionTypeDesc;
    @Transient
    private String currencyCodeDesc;
    @Transient
    private Double writeOffAmount;
    @Transient
    private String postedFlag;
    /**
     * 二期功能：可退金额
     */
    @Transient
    private Double canReturnAmount;
    /**
     * 二期功能：冻结金额
     */
    @Transient
    private Double blockAmount;
    /**
     * 二期功能：已退款金额
     */
    @Transient
    private Double returnWriteOffAmount;

    /**
     * 二期功能：现金事务表IDs
     */
    @Transient
    private Long[] transactionIds;

    /**
     * 二期功能：付款对象
     */
    private Long bpId;

    /**
     * 二期功能：权限字段
     */
    private String authorityRuleString;

    /**
     * 二期功能：行ID
     */
    @Transient
    private Long lnId;

    /**
     * 二期功能：源现金事务ID
     */
    @Transient
    private Long sourceTransactionId;

    @Transient
    private String refundingAmount;   // 退款中金额
    @Transient
    private String bpType;  //对象类别
    @Transient
    private String bpTypeN; //对象类别
    @Transient
    private Long paymentBpId;
    @Transient
    private String writeOffFlag; //现金流核销状态
    @Transient
    private String writeOffFlagN; //现金流核销状态
    @Transient
    private Long times; //现金流期数
    @Transient
    private Date dueDate; //现金流应收日
    @Transient
    private Double dueAmount; //现金流应收金额
    @Transient
    private Double receivedAmount; //现金流已收金额
    @Transient
    private Double receivedCompAmount; //现金流已收代偿款
    @Transient
    private Long transactionId; //现金事务ID
    @Transient
    private Long cashflowId; //现金流ID
    @Transient
    private List<Long> cashflowIdS;

    private Long processInstanceId;  //退款申请工作流ID
    private String refundStatus; //退款申请工作流状态

}
