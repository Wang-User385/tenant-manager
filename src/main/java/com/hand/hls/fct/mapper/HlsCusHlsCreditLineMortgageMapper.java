package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineMortgage;

import java.util.List;

public interface HlsCusHlsCreditLineMortgageMapper extends Mapper<HlsCusHlsCreditLineMortgage> {

    List<HlsCusHlsCreditLineMortgage> queryCreditLineMortgageByCreditLineId(HlsCusHlsCreditLineMortgage dto);
}
