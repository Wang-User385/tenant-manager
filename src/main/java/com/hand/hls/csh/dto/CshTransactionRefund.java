package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.naming.ldap.PagedResultsControl;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/4/24
 * @description: 收款退款表
 */
@Getter
@Setter
@ExtensionAttribute(disable = true)
@Table(name = "csh_transaction_refund")
public class CshTransactionRefund extends BaseDTO {
    @Id
    @GeneratedValue
    private Long refundId;

    private Long transactionId;
    private String  refundNumber ;
    private Date refundDate;
    private Double refundAmount;
    private String refundStatus;
    private Long bankAccountId;
    private String periodNumber;
    private String currencyCode;
    private String bankSlipNum;
    private Long bpBankAccountId;
    private Long processInstanceId;
    private String description;
    private String refundFlag;
    private Long handlingDepartmentId;
    private String handlingDepartment;
    private Long financialAgentId;
    private String financialAgent;
    private String paymentDescription;
    private Long paymentProcessInstanceId;
    private String paymentRefundStatus;
    private Date actualPaymentDate;


}
