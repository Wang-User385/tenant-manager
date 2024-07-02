package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;

import java.util.List;

public interface HlsCusFundingPlanLnMapper extends FundingPlanLnMapper<HlsCusFundingPlanLn>{

    List<HlsCusFundingPlanLn> selectPaymentPlanInfo(HlsCusFundingPlanLn hlsCusFundingPlanLn);

    List<HlsCusFundingPlanLn> queryPaymentPlan(HlsCusFundingPlanLn hlsCusFundingPlanLn);
}