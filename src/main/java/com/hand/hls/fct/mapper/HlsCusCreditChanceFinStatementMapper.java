package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusCreditChanceFinStatement;

import java.util.List;

public interface HlsCusCreditChanceFinStatementMapper extends Mapper<HlsCusCreditChanceFinStatement> {

    List<HlsCusCreditChanceFinStatement> selectByChanceId(HlsCusCreditChanceFinStatement dto);
}
