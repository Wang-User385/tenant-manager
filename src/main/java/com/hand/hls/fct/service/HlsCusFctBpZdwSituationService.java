package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusFctBpZdwSituation;

import java.util.List;

public interface HlsCusFctBpZdwSituationService extends IBaseService<HlsCusFctBpZdwSituation>, ProxySelf<HlsCusFctBpZdwSituationService> {

    List<HlsCusFctBpZdwSituation> fctBpZdwSituationInfoQuery(IRequest iRequest, HlsCusFctBpZdwSituation hlsCusFctBpZdwSituation, int page, int pageSize);

}