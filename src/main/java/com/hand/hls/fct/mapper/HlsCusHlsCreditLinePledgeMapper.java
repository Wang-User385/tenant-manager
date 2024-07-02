package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditLinePledge;

import java.util.List;

public interface HlsCusHlsCreditLinePledgeMapper extends Mapper<HlsCusHlsCreditLinePledge> {

    List<HlsCusHlsCreditLinePledge> queryCreditLinePledgeByCreditLineId(HlsCusHlsCreditLinePledge dto);
}
