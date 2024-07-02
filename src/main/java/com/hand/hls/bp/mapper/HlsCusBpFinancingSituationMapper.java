package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpFinancingSituation;

import java.util.List;

public interface HlsCusBpFinancingSituationMapper extends Mapper<HlsCusBpFinancingSituation> {

    List<HlsCusBpFinancingSituation> queryAll(HlsCusBpFinancingSituation bpFinancingSituation);

    void deleteFinancingByBpId(Long bpId);

}