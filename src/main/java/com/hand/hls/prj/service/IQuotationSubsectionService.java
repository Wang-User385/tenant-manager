package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.QuotationSubsection;

public interface IQuotationSubsectionService extends IBaseService<QuotationSubsection>, ProxySelf<IQuotationSubsectionService> {
    //更新quotation数据
    void updateQuotationBySubsectionData(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation) throws IllegalArgumentException;
}