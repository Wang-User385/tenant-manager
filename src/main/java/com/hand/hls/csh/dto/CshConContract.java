package com.hand.hls.csh.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by lpc on 2017/9/30.
 */
@Getter
@Setter
public class CshConContract extends BaseDTO {
    @Id
    @GeneratedValue
    private Long contractId;
    private Long companyId;
    private Long projectId;
    private Long quotationId;
    private String documentType;
    private String documentCategory;
    private String businessType;
    private String contractNumber;
    private String contractName;
    private String contractStatus;
    private String leaseChannel;
    private String division;
    private Long leaseTimes;

    private Date signDate;
    private Date inceptionOfLease;
    private Long employeeId;
    private Long unitId;
    private Date leaseStartDate;
    private Date firstPayDate;
    private Date lastPayDate;
    private Date leaseEndDate;
    private Date originalRecallDate;
    private Date leaseCardRecallDate;
    private Date earlyTerminationDate;
    private Date terminationDate;
    private Date assignmentDate;
    private Long annualPayTimes;
    private Double leaseTerm;
    private Long payType;
    private String currency;
    private Long taxTypeId;
    private String vatFlag;
    private Double vatRate;
    private Double leaseItemAmount;
    private Double vatInput;
    private Long vatInputTaxTypeId;
    private Double vatInputTaxTypeRate;
    private Double downPaymentRatio;
    private Double downPayment;
    private Double netDownPayment;
    private Double vatDownPayment;
    private Double financeAmount;
    private Double netFinanceAmount;
    private Double vatFinanceAmount;
    private Double totalRental;
    private Double netTotalRental;
    private Double vatTotalRental;
    private Double totalInterest;
    private Double netTotalInterest;
    private Double vatTotalInterest;
    private Double leaseChargeRatio;
    private Double leaseCharge;
    private Double netLeaseCharge;
    private Double vatLeaseCharge;
    private Double leaseMgtFeeRatio;
    private Double leaseMgtFee;
    private Double netLeaseMgtFee;
    private Double vatLeaseMgtFee;
    private String leaseMgtFeeRule;
    private Double depositRatio;
    private Double deposit;
    private String depositDeduction;
    private Double residualRatio;
    private Double residualValue;
    private Double netResidualValue;
    private Double vatResidualValue;
    private Double balloonRatio;
    private Double balloon;
    private Double netBalloon;
    private Double vatBalloon;
    private String baseRateType;
    private Double baseRate;
    private Double intRate;
    private Double intRateImplicit;
    private String intRateType;
    private Double irr;
    private Double irrAfterTax;
    private Double pmt;
    private Double pmtFirst;
    private Double annualMeanRate;
    private String receivedStatus;
    private String overdueStatus;
    private String billingMethod;
    private String billingStatus;
    private String bizDayConvention;
    private String penaltyProfile;
    private Long bpAccountId;
    @Transient
    private String organization1;

    @Transient
    private Double paymentAmount;//抵扣付款金额

    @Transient
    private Double deductAmount;//抵扣金额

    @Transient
    private String cfItem;
    @Transient
    private String cfType;
    @Transient
    private String tenantBpType;
    @Transient
    private Long days;
    @Transient
    private Long bpId;
    @Transient
    private Date transactionDate;
    @Transient
    private String bpName;

    @Transient
    private Long times;
    @Transient
    private String description;
    @Transient
    private Date dueDate;
    @Transient
    private Double dueAmount;
    @Transient
    private Double receivedAmount;
    @Transient
    private Double unpaidAmount;
    @Transient
    private String bankAccountNum;
    @Transient
    private String bankAccountName;
    @Transient
    private String companyName;
    @Transient
    private Double sumDueAmount;
    @Transient
    private Double sumReceivedAmount;
    @Transient
    private Double sumUnpaidAmount;
    @Transient
    private BigInteger latestHours;

    @Transient
    private Double outSumAmount;
    @Transient
    private Double inSumAmount;
    @Transient
    private Double newNum;
    @Transient
    private Double signNum;
    @Transient
    private Double inceptNum;
    @Transient
    private Double pendingNum;
    @Transient
    private Double terminationNum;
    @Transient
    private String writeOffFlag;

    @Transient
    private String signStatus;
    @Transient
    private String inceptStatus;
    @Transient
    private String changeStatus;
    @Transient
    private String cancelStatus;
    @Transient
    private Double sumAmount;

    @Transient
    private Date day;
    @Transient
    private Double amount;
    @Transient
    private Long contractCount;

    @Transient
    private Long stockContractCount;
    @Transient
    private Long newContractCount;

    @Transient
    private Long cashflowHCount;
    @Transient
    private Long guarantorHCount;
    @Transient
    private Long itemHCount;
    @Transient
    private Long mortgageHCount;
    @Transient
    private Long tenantHCount;
    @Transient
    private String contractReqType;
    @Transient
    private String changeReqDate;
    @Transient
    private String status;
    @Transient
    private String bpNumber;
    @Transient
    private Double transactionAmount;
    @Transient
    private Long dueAmountCount;
    @Transient
    private Long receivedAmountCount;
    @Transient
    private Long transactionAmountCount;
    @Transient
    private Long paymentReqLnId;
    @Transient
    private String userName;
    @Transient
    private String amountPaid;
    @Transient
    private String bpBankAccountName;
    @Transient
    private String bpBankName;
    @Transient
    private String bpBankAccountNum;
    @Transient
    private Date paymentReqDate;
    @Transient
    private Date writeOffDate;
    @Transient
    private Double writeOffDueAmount;
    @Transient
    private String companyFullName;
    @Transient
    private Long typeCount;
    @Transient
    private Long queryTimeSolt;
    @Transient
    private Long stockCount; // 已存在合同数量
    @Transient
    private Long newCount;// 新增合同数量
    @Transient
    private Long paymentReqId;
    @Transient
    private Long cshTransactionId;
    @Transient
    private Long transactionId;
    @Transient
    private Double writeOffAmount;
    @Transient
    private Long changeReqId;
    @Transient
    private String bankName;
    @Transient
    private String queryCondition;
    @Transient
    private String contractStatusValue;
    @Transient
    private String divisionDes;
    @Transient
    private String paymentAppFlag;
    @Transient
    private String collectFlag;
    @Transient
    private String receiveDateFrom;
    @Transient
    private String receiveDateTo;
    @Transient
    private String overdueFrom;
    @Transient
    private String overdueTo;
    @Transient
    private String division_name;
    @Transient
    private String document_type_name;
    @Transient
    private String priceList;
    @Transient
    private Double principal;
    @Transient
    private Double interest;
    @Transient
    private Double outstanding_principal;
    @Transient
    private String bpTypeDesc;
    @Transient
    private String sourceDocCategory;
    @Transient
    private String writeOffType;//核销类型
    @Transient
    private String transactionType;//现金事物类型

    @Transient
    private Double sumPrincipal;
    @Transient
    private Double sumInterest;

    @Transient
    private String terminationStatus;

    @Transient
    private String created_by_desc;

    @Transient
    private String creation_date_desc;
    @Transient
    private String document_type_desc;
    @Transient
    private String business_type_desc;

    @Transient
    private String division_desc;

    @Transient
    private String lease_channel_desc;

    @Transient
    private Long expired_times;
    @Transient
    private Long payment_times;
    @Transient
    private Long unpayment_times;
    @Transient
    private Long overdue_times;
    @Transient
    private Double avg_overdue_dates;
    @Transient
    private Double max_overdue_dates;
    @Transient
    private Double total_payment_amount;
    @Transient
    private String price_list;
    @Transient
    private String price_list_desc;

    @Transient
    private Date create_date;
    @Transient
    private Long contract_incept_id;

    @Transient
    private String annual_pay_times_desc;
    @Transient
    private Date createDate;
    @Transient
    private String int_rate_fixing_way;

    @Transient
    private Double int_rate_fixing_range;

    @Transient
    private Double total_received_amount;
    @Transient
    private String down_payment_way;
    @Transient
    private Double down_payment_range;

    @Transient
    private String deposit_way;

    @Transient
    private Double deposit_range;

    @Transient
    private Double total_overdue_dates;


    @Transient
    private String terminationFLag;

    @Transient
    private String paymentFlag;

    @Transient
    private String documentCategoryDesc;

    private String legalContractNumber;

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HlsCusCshConContract [contract_id=" + contractId + ", company_id=" + companyId + ", project_id="
                + projectId + ", quotation_id=" + quotationId + ", document_type=" + documentType
                + ", document_category=" + documentCategory + ", business_type=" + businessType + ", contract_number="
                + contractNumber + ", contract_name=" + contractName + ", contract_status=" + contractStatus
                + ", lease_channel=" + leaseChannel + ", division=" + division + ", cf_item=" + cfItem + ", cf_type="
                + cfType + ", bp_id=" + bpId + ", bp_name=" + bpName + ", times=" + times + ", description="
                + description + ", due_date=" + dueDate + ", due_amount=" + dueAmount + ", received_amount="
                + receivedAmount + ", unpaid_amount=" + unpaidAmount + "]";
    }


}
