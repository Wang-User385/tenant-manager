package com.hand.hls.calc.service;

import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;

public interface QuotationCommon {
    String getSourceDocumentCategory();

    HlsCusPrjQuotation PrjQuotationSubmit(IRequest var1, HlsCusPrjQuotation var2) throws Exception;
}