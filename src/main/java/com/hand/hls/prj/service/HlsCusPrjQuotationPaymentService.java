package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectInfo;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import hls.core.utils.exception.HlsCusException;

import java.util.List;
import java.util.Map;

public interface HlsCusPrjQuotationPaymentService extends IBaseService<HlsCusPrjQuotation>, ProxySelf<HlsCusPrjQuotationPaymentService> {

    void quotationReCalc(IRequest request, Long quotationId) throws Exception;

    void updateQuotaion(HlsCusPrjQuotation quotation, List<Map> maps, HlsCalcConfig hlsCalcConfig) throws Exception;


    void updateXirr(IRequest request, HlsCusPrjQuotation prjQuotation) throws IllegalArgumentException;

}