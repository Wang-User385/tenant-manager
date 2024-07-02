package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusHlsCreditChanceMortgage;

import java.util.List;

public interface HlsCusHlsCreditChanceMortgageService extends IBaseService<HlsCusHlsCreditChanceMortgage>, ProxySelf<HlsCusHlsCreditChanceMortgageService> {
    /**
     *
     * @param requestContext
     * @param chanceMortgage
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusHlsCreditChanceMortgage> selectByForeignKey(IRequest requestContext, HlsCusHlsCreditChanceMortgage chanceMortgage, Integer page, Integer pageSize);
}