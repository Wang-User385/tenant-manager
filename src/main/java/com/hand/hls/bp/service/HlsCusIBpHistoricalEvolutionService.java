package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpHistoricalEvolution;

import java.util.List;

public interface HlsCusIBpHistoricalEvolutionService extends IBaseService<HlsCusBpHistoricalEvolution>, ProxySelf<HlsCusIBpHistoricalEvolutionService> {

    List<HlsCusBpHistoricalEvolution> selectAll(IRequest requestContext, HlsCusBpHistoricalEvolution bpHistoricalEvolution, int page, int pagesize);

}