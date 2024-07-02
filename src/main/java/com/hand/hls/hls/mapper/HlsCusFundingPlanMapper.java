package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsCusFundingPlan;

import java.util.List;

public interface HlsCusFundingPlanMapper extends FundingPlanMapper<HlsCusFundingPlan>{
    List<HlsCusFundingPlan> queryForFundingPlanDetail(HlsCusFundingPlan hlsCusFundingPlan);
    List<HlsCusFundingPlan> queryForFundingPlan(HlsCusFundingPlan hlsCusFundingPlan);

    List<HlsCusFundingPlan> querFundByContracrId(HlsCusFundingPlan hlsCusFundingPlan);
}