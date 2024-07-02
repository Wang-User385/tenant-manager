package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fp.dto.JcFundingPlanSecond;

import java.util.List;

public interface JcFundingPlanSecondService extends IBaseService<JcFundingPlanSecond>, ProxySelf<JcFundingPlanSecondService>{
    List<JcFundingPlanSecond> selectAll(IRequest iRequest, JcFundingPlanSecond jcFundingPlanSecond, int page, int pageSize);
}