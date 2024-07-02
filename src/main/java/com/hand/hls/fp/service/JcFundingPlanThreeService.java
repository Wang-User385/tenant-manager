package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fp.dto.JcFundingPlanThree;

import java.util.List;

public interface JcFundingPlanThreeService extends IBaseService<JcFundingPlanThree>, ProxySelf<JcFundingPlanThreeService>{
    List<JcFundingPlanThree> selectAll(IRequest iRequest,JcFundingPlanThree jcFundingPlanThree,int page,int pageSize);
}