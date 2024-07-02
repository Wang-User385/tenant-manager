package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpZdwSituation;

import java.util.List;

public interface HlsCusBpZdwSituationMapper extends Mapper<HlsCusBpZdwSituation> {

    List<HlsCusBpZdwSituation> queryAll(HlsCusBpZdwSituation bpZdwSituation);

}