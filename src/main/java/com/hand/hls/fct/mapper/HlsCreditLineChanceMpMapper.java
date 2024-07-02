package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCreditLineChanceMp;

import java.util.List;

public interface HlsCreditLineChanceMpMapper extends Mapper<HlsCreditLineChanceMp>{
    List<HlsCreditLineChanceMp> queryCredLineChance(HlsCreditLineChanceMp hlsCreditLineChanceMp);
}