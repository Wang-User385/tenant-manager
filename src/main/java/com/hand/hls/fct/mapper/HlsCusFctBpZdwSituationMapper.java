package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusFctBpZdwSituation;

import java.util.List;

public interface HlsCusFctBpZdwSituationMapper extends Mapper<HlsCusFctBpZdwSituation> {

    List<HlsCusFctBpZdwSituation> fctBpZdwSituationInfoQuery(HlsCusFctBpZdwSituation hlsCusFctBpZdwSituation);

}