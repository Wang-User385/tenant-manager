package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;


@SuppressWarnings("serial")
@ExtensionAttribute(disable = true)
@Table(name = "con_contract")
@Getter
@Setter
public class HlsCusCshConContract extends CshConContract {

    @Transient
    private String companySpvN;
    @Transient
    private Long companySpv;
@Transient
    private String fundingPlanNumber;
    @Transient
    private String bpTypeN;
    @Transient
    private String bpType;

    @Transient
    private String paymentReqNumber;

    @Transient
    private String defrayNumber;

    @Transient
    private Date proposedLaunchDate;
    @Transient
    private Date proposedLaunchDateFrom;
    @Transient
    private Date proposedLaunchDateTo;

    @Transient
    private String  applicationName;

    @Transient
    private Date applicationDate;

    public String getBpTypeN() {
        return bpTypeN;
    }

    public void setBpTypeN(String bpTypeN) {
        this.bpTypeN = bpTypeN;
    }

    public String getBpType() {
        return bpType;
    }

    public void setBpType(String bpType) {
        this.bpType = bpType;
    }

    public String getPaymentReqNumber() {
        return paymentReqNumber;
    }

    public void setPaymentReqNumber(String paymentReqNumber) {
        this.paymentReqNumber = paymentReqNumber;
    }

    @Transient
    private String statusN;

    @Transient
    private String fundingPlanId;

    @Transient
    private Double loanTotalAmount;

    @Transient
    private Double loanNetAmount;

    @Transient
    private Long processInstanceId;
    @Transient
    private String loanType;
    @Transient
    private String paymentType;
    @Transient
    private String paymentApprovedStatus;
    @Transient
    private String paymentApprovedStatusN;
    @Transient
    private String loanTypeN;
    @Transient
    private String paymentTypeN;

    @Transient
    private String projectManagerIdN;

    @Transient
    private Double unpaid;

    private Long projectAssistant2;

    private Double proposerEmployeeRatio;

    private Double projectAssistantRatio;

    private Double projectAssistant2Ratio;

    private Long riskHost;
    private Double riskHostRatio;
    private Long riskAssistantFirst;
    private Long riskAssistantSecond;
    private Double riskAssistantFirstRatio;
    private Double riskAssistantSecondRatio;
    @Transient
    private String projectAssistant2Name;
    @Transient
    private String projectAssistant2N;
    @Transient
    private String riskHostN;
    @Transient
    private String riskAssistantFirstN;
    @Transient
    private String riskAssistantSecondN;

    private Long legalHost;
    private Double legalHostRatio;
    private Long legalAssistantFirst;
    private Long legalAssistantSecond;
    private Double legalAssistantFirstRatio;
    private Double legalAssistantSecondRatio;
    @Transient
    private String legalHostN;
    @Transient
    private String legalAssistantFirstN;
    @Transient
    private String legalAssistantSecondN;
}
