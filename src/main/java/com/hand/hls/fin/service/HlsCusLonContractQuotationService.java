package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusLonContractQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;

import java.util.List;

public interface HlsCusLonContractQuotationService extends IBaseService<HlsCusLonContractQuotation>, ProxySelf<HlsCusLonContractQuotationService> {
    List<HlsCusPrjQuotation> createCalcByWithdraw(IRequest iRequest, Long withdrawId, String priceList) throws Exception;

    void saveCalcFront(IRequest iRequest,HlsCusPrjQuotation quotation, String sheets) throws Exception;
}