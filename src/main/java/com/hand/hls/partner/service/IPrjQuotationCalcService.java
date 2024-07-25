package com.hand.hls.partner.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;

public interface IPrjQuotationCalcService extends IBaseService<HlsCusPrjQuotation>, ProxySelf<HlsCusPrjQuotationService> {

    void prjQuotationCalc(Long quotationId,IRequest iRequest) throws Exception;
}
