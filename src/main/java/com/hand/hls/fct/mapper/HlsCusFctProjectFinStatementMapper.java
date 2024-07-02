package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusFctProjectFinStatement;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;

import java.util.List;

public interface HlsCusFctProjectFinStatementMapper extends Mapper<HlsCusFctProjectFinStatement> {
     public List<HlsCusFctProjectFinStatement> queryFinStatementByBpId(HlsCusHlsCreditLine cusHlsCreditLine);
}


