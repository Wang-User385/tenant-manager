package com.hand.hls.prj.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;

import java.util.List;

public interface HlsCusPrjQuotationDetailsService extends IBaseService<HlsCusPrjQuotationDetails>, ProxySelf<HlsCusPrjQuotationDetailsService> {

    List<HlsCusPrjQuotationDetails> queryDetailsById(HlsCusPrjQuotationDetails prjQuotationDetails);

    int deleteDetailsById(HlsCusPrjQuotation hlsCusPrjQuotation);
}