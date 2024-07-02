package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditChancePledge;

import java.util.List;

public interface HlsCusHlsCreditChancePledgeMapper extends Mapper<HlsCusHlsCreditChancePledge> {

    List<HlsCusHlsCreditChancePledge> selectByForeignKey(HlsCusHlsCreditChancePledge chancePledge);
}