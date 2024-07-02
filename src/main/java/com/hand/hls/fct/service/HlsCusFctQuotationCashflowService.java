package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface HlsCusFctQuotationCashflowService extends IBaseService<HlsCusFctQuotationCashflow>, ProxySelf<HlsCusFctQuotationCashflowService> {

    List<HlsCusFctQuotationCashflow> selectNoticePrint(IRequest request, HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow, Integer page, Integer pageSize);

    ResponseData downloadNoticePrintFile(List<HlsCusFctQuotationCashflow> fctQuotationCashflowList, IRequest requestContext, HttpServletRequest request, HttpServletResponse response);

}
