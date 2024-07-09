package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBusinessConditionHydropower;

import java.util.List;


public interface HlsBusinessConditionHydropowerMapper extends Mapper<HlsBusinessConditionHydropower>{
    List<HlsBusinessConditionHydropower> queryAllByConditionId(HlsBusinessConditionHydropower hlsBusinessConditionHydropower);
}