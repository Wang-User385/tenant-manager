package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fp.dto.JcFundingPlan;
import com.hand.hls.fp.dto.JcFundingPlanOne;

import java.util.List;

public interface JcFundingPlanOneService extends IBaseService<JcFundingPlanOne>, ProxySelf<JcFundingPlanOneService>{

    List<JcFundingPlanOne> selectAll(IRequest iRequest, JcFundingPlanOne jcFundingPlanOne, int page, int pageSize);
}