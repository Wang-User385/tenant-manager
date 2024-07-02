package com.hand.hls.prj.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @Author Robert8900
 * @Date: 2019/8/20 10:07
 * @Description:报价分段测算表
 * @Purpose: 分段报价
 **/

@ExtensionAttribute(disable = true)
@Table(name = "prj_quotation_subsection")
public class HlsCusPrjQuotationSubsection extends BaseDTO {
    @Id
    @GeneratedValue
    private Long subsectionId;

    @NotNull
    private Long quotationId;

    private Long startTime;

    private Long endTime;

    private String calcWay;

    private Double amount;

    private Long annualPayTimes;

    private Long intervalMonths;

    public Long getSubsectionId() {
        return subsectionId;
    }

    public void setSubsectionId(Long subsectionId) {
        this.subsectionId = subsectionId;
    }

    public Long getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(Long quotationId) {
        this.quotationId = quotationId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getCalcWay() {
        return calcWay;
    }

    public void setCalcWay(String calcWay) {
        this.calcWay = calcWay;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getAnnualPayTimes() {
        return annualPayTimes;
    }

    public void setAnnualPayTimes(Long annualPayTimes) {
        this.annualPayTimes = annualPayTimes;
    }

    public Long getIntervalMonths() {
        return intervalMonths;
    }

    public void setIntervalMonths(Long intervalMonths) {
        this.intervalMonths = intervalMonths;
    }
}
