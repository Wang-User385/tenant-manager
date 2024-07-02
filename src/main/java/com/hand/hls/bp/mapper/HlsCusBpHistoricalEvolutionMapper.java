package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpHistoricalEvolution;

import java.util.List;

public interface HlsCusBpHistoricalEvolutionMapper extends Mapper<HlsCusBpHistoricalEvolution> {

    List<HlsCusBpHistoricalEvolution> queryAll(HlsCusBpHistoricalEvolution bpHistoricalEvolution);


}