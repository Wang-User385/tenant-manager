package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditChanceMortgage;

import java.util.List;

public interface HlsCusHlsCreditChanceMortgageMapper extends Mapper<HlsCusHlsCreditChanceMortgage> {

    List<HlsCusHlsCreditChanceMortgage> selectByForeignKey(HlsCusHlsCreditChanceMortgage chanceMortgage);
}