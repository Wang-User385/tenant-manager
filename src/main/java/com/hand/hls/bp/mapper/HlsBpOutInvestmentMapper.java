package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpOutInvestment;

import java.util.List;

public interface HlsBpOutInvestmentMapper extends Mapper<HlsBpOutInvestment>{

    List<HlsBpOutInvestment> queryHlsBpOutInvestment(HlsBpOutInvestment hlsBpOutInvestment);
}