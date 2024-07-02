package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.CalcPrice;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;

import java.util.List;

public interface ICalcPriceService extends IBaseService<CalcPrice>, ProxySelf<ICalcPriceService>{

    List<CalcPrice> svaeCalcPrice(IRequest iRequest, List<CalcPrice> list);

    List<HlsCusPrjQuotation> savePrjCalcPrice(IRequest iRequest, List<HlsCusPrjQuotation> hlsCusPrjQuotationList);

}