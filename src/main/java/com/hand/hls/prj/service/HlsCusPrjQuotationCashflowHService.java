package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflowH;

import java.util.Date;

/**
 * @Author Robert8900
 * @Date: 2019/8/20 17:06
 * @Description:
 * @Purpose:
 **/
public interface HlsCusPrjQuotationCashflowHService extends IBaseService<HlsCusPrjQuotationCashflowH>, ProxySelf<HlsCusPrjQuotationCashflowHService> {
    /*EQUAL_INTEREST  等额租金 计算报价*/
    void calcEqualInterest(IRequest iRequest, Long quotationId, Double outstandingPrincipal, Date leaseStartDate, Double leaseTerm, Long annualPayTimes, Double vatRate, Double intRate, Long payType, Long subsectionId, String calcWay, String businessType, Long fristTimesCount, Date inceptionOfLease) throws IllegalArgumentException;

    /*EQUAL_PRINCIPAL 等额本金 计算报价*/
    void calcEqualPrincipal(IRequest iRequest, Long quotationId, Double outstandingPrincipal, Date leaseStartDate, Double leaseTerm, Long annualPayTimes, Double vatRate, Double intRate, Long payType, Long subsectionId, String calcWay, String businessType, Long fristTimesCount, Date inceptionOfLease) throws IllegalArgumentException;

    /*DESIGNATED_RENT 指定租金 计算报价*/
    void calcDesignatedRent(IRequest iRequest, Long quotationId, Double outstandingPrincipal, Date leaseStartDate, Double leaseTerm, Long annualPayTimes, Double vatRate, Double intRate, Double dueAmount, Long payType, Long subsectionId, String calcWay, String businessType, Long fristTimesCount, Date inceptionOfLease) throws IllegalArgumentException;

    /*DESIGNATED_PRINCIPAL  指定本金 计算报价*/
    void calcDesignatedPrincipal(IRequest iRequest, Long quotationId, Double outstandingPrincipal, Date leaseStartDate, Double leaseTerm, Long annualPayTimes, Double vatRate, Double intRate, Double principal, Long payType, Long subsectionId, String calcWay, String businessType, Long fristTimesCount, Date inceptionOfLease) throws IllegalArgumentException;
}
