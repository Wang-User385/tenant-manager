package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpZdwSituation;

import java.util.List;

public interface HlsCusIBpZdwSituationService extends IBaseService<HlsCusBpZdwSituation>, ProxySelf<HlsCusIBpZdwSituationService> {

    List<HlsCusBpZdwSituation> selectAll(IRequest requestContext, HlsCusBpZdwSituation bpZdwSituation, int page, int pagesize);

}