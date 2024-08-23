package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCreditLineChanceCondition;

import java.util.List;

public interface HlsCreditLineChanceConditionMapper extends Mapper<HlsCreditLineChanceCondition>{
        List<HlsCreditLineChanceCondition>     queryAll();
}