package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusFctBpFinancingSituation;

import java.util.List;

public interface HlsCusFctBpFinancingSituationService extends IBaseService<HlsCusFctBpFinancingSituation>, ProxySelf<HlsCusFctBpFinancingSituationService> {

    List<HlsCusFctBpFinancingSituation> fctBpFinancingSituationInfoQuery(IRequest iRequest, HlsCusFctBpFinancingSituation hlsCusFctBpFinancingSituation, int page, int pageSize);

}