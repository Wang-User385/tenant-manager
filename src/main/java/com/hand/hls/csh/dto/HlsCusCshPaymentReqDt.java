//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "csh_payment_req_dt"
)

@Getter
@Setter
public class HlsCusCshPaymentReqDt extends CshPaymentReqDt {
    public HlsCusCshPaymentReqDt() {
    }

    @Transient
    private String sourceDocLineIdN;

    private Long fundingPlanLnId;
    @Transient
    private Double remainingUncollectedAmount;

    @Transient
    private Long cfType;

    public Double getRemainingUncollectedAmount() {
        return remainingUncollectedAmount;
    }

    public void setRemainingUncollectedAmount(Double remainingUncollectedAmount) {
        this.remainingUncollectedAmount = remainingUncollectedAmount;
    }

    public String getSourceDocLineIdN() {
        return sourceDocLineIdN;
    }

    public void setSourceDocLineIdN(String sourceDocLineIdN) {
        this.sourceDocLineIdN = sourceDocLineIdN;
    }

    public Long getFundingPlanLnId() {
        return fundingPlanLnId;
    }

    public void setFundingPlanLnId(Long fundingPlanLnId) {
        this.fundingPlanLnId = fundingPlanLnId;
    }

    public Long getCfType() {
        return cfType;
    }

    public void setCfType(Long cfType) {
        this.cfType = cfType;
    }
}
