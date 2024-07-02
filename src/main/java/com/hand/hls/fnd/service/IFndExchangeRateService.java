package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.FndExchangeRate;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import hls.core.utils.exception.HlsCusException;

public interface IFndExchangeRateService extends IBaseService<FndExchangeRate>, ProxySelf<IFndExchangeRateService>{


    Double selectExchangeRate(IRequest iRequest, FndExchangeRate fndExchangeRate) throws HlsCusException;

}