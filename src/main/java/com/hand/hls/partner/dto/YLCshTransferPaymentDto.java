package com.hand.hls.partner.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;

import java.util.Date;

@Data
@ExtensionAttribute(disable = true)
@Table(name = "YL_CSH_TRANSFER_PAYMENT")
public class YLCshTransferPaymentDto extends BaseDTO {

    public static final String FIELD_PAYMENT_ID = "paymentId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_CASHFLOW_ID = "cashflowId";
    public static final String FIELD_REPAY_PRINCIPAL = "repayPrincipal";
    public static final String FIELD_REPAY_INTEREST = "repayInterest";
    public static final String FIELD_REPAY_PENALTY = "repayPenalty";
    public static final String FIELD_REPAY_AMOUNT = "repayAmount";
    public static final String FIELD_REPAY_DATE = "repayDate";
    public static final String FIELD_CONFIRM_DATE = "confirmDate";
    public static final String FIELD_TRANSFER_PAYMENT_STATUS = "transferPaymentStatus";
    public static final String FIELD_BANK_STATEMENT = "bankStatement";


    private Long paymentId;

    private Long contractId;

    private Long cashflowId;

    private Double repayPrincipal;

    private Double repayInterest;

    private Double repayPenalty;

    private Double repayAmount;

    private Date repayDate;

    private Date confirmDate;

    @Length(max = 24)
    private String transferPaymentStatus;

    @Length(max = 64)
    private String bankStatement;

    @Transient
    private String contractNumber;

    @Transient
    private String tenantIdN;

    @Transient
    private Long times;

    @Transient
    private String transferPaymentStatusN;

    @Transient
    private String transactionNum;

    @Transient
    private String writeOffFlagN;

    @Transient
    private Date repayDateFrom;

    @Transient
    private Date repayDateTo;

    @Transient
    private Long projectId;

}
