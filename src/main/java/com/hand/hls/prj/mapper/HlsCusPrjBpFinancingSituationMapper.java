package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjBpFinancingSituation;

import java.util.List;

public interface HlsCusPrjBpFinancingSituationMapper extends Mapper<HlsCusPrjBpFinancingSituation> {

    List<HlsCusPrjBpFinancingSituation> prjBpFinancingSituationInfoQuery(HlsCusPrjBpFinancingSituation hlsCusPrjBpFinancingSituation);

}