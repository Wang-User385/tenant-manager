package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.FundingPlan;
import com.hand.hls.hls.dto.HlsCusFundingPlan;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;

import java.util.List;

public interface IFundingPlanService extends IBaseService<HlsCusFundingPlan>, ProxySelf<IFundingPlanService>{
    HlsCusFundingPlan createFundingPlan(IRequest iRequest,HlsCusFundingPlan hlsCusFundingPlan);
    List<HlsCusFundingPlan> fundingPlanSubmit(IRequest iRequest, HlsCusFundingPlan hlsCusFundingPlan)throws ResMessageException, ParameterNullException;
    List<HlsCusFundingPlanLn> selectPlan(IRequest iRequest, List<HlsCusFundingPlanLn> hlsCusFundingPlanLns)throws ResMessageException;
    List<HlsCusFundingPlan> removePlan(IRequest iRequest, List<HlsCusFundingPlan> hlsCusFundingPlans)throws ResMessageException;
    HlsCusFundingPlan fundingPlanCheck(IRequest iRequest, HlsCusFundingPlan hlsCusFundingPlan)throws ResMessageException;
    HlsCusFundingPlan saveFundingPlan(IRequest iRequest, HlsCusFundingPlan hlsCusFundingPlan);
}