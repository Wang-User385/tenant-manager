package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationSubsection;

import java.util.Date;

/**
 * @Author Robert8900
 * @Date: 2019/8/20 10:14
 * @Description:
 * @Purpose:
 **/
public interface HlsCusPrjQuotationSubsectionService extends IBaseService<HlsCusPrjQuotationSubsection>, ProxySelf<HlsCusPrjQuotationSubsectionService> {
    void calcPrjQuotation(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation, String changeFlag, Double changeFinanceAmount, Double changeLeaseTerm) throws IllegalArgumentException;

    String getStartDate(Date date);

    String getEndDate(String day, Date endDate);
    Long getDays(String startDate, String endDate);

}
