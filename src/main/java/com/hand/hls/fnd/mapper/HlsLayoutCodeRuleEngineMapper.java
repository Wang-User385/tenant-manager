package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsLayoutCodeRuleEngine;

import java.util.HashMap;
import java.util.List;

public interface HlsLayoutCodeRuleEngineMapper extends Mapper<HlsLayoutCodeRuleEngine> {

    List<HlsLayoutCodeRuleEngine> query(HlsLayoutCodeRuleEngine dto);

    List<Long> queryFunctionRoute(HashMap mapper);

    List<String> queryFunctionResult(Long routeId);

}