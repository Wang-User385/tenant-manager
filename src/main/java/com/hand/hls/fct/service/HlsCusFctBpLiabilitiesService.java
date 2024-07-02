package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusFctBpLiabilities;

import java.util.List;

public interface HlsCusFctBpLiabilitiesService extends IBaseService<HlsCusFctBpLiabilities>, ProxySelf<HlsCusFctBpLiabilitiesService> {

    List<HlsCusFctBpLiabilities> fctBpLiabilitiesInfoQuery(IRequest iRequest, HlsCusFctBpLiabilities hlsCusFctBpLiabilities, int page, int pageSize);

}