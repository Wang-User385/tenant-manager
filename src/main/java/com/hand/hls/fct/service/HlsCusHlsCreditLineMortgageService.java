package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineMortgage;

import java.util.List;

public interface HlsCusHlsCreditLineMortgageService extends IBaseService<HlsCusHlsCreditLineMortgage>, ProxySelf<HlsCusHlsCreditLineMortgageService> {

    List<HlsCusHlsCreditLineMortgage> queryCreditLineMortgage(IRequest iRequest, HlsCusHlsCreditLineMortgage hlsCusHlsCreditLineMortgage, int page, int pageSize);
}
