package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusFctBpLiabilities;

import java.util.List;

public interface HlsCusFctBpLiabilitiesMapper extends Mapper<HlsCusFctBpLiabilities> {

    List<HlsCusFctBpLiabilities> fctBpLiabilitiesInfoQuery(HlsCusFctBpLiabilities hlsCusFctBpLiabilities);
}