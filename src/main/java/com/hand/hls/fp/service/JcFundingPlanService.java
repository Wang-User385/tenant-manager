package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fp.dto.JcFundingPlan;

import java.util.List;

public interface JcFundingPlanService extends IBaseService<JcFundingPlan>, ProxySelf<JcFundingPlanService>{

    List<JcFundingPlan> selectAll(IRequest iRequest, JcFundingPlan jcFundingPlan, int page, int pageSize);
}