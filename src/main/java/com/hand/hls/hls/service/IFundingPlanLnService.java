package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.FundingPlanLn;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.utils.ResMessageException;

import java.util.List;

public interface IFundingPlanLnService extends IBaseService<HlsCusFundingPlanLn>, ProxySelf<IFundingPlanLnService>{

    void insertDt(IRequest iRequest,List<HlsCusFundingPlanLn> hlsCusFundingPlanLns);

    List<HlsCusFundingPlanLn> removePlanLn(IRequest iRequest, List<HlsCusFundingPlanLn> hlsCusFundingPlanLns)throws ResMessageException;

}