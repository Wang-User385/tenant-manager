package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusFctBpFinancingSituation;

import java.util.List;

public interface HlsCusFctBpFinancingSituationMapper extends Mapper<HlsCusFctBpFinancingSituation> {

    List<HlsCusFctBpFinancingSituation> fctBpFinancingSituationInfoQuery(HlsCusFctBpFinancingSituation hlsCusFctBpFinancingSituation);

}