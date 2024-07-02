package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fp.dto.JcFundingPlanFour;

import java.util.List;

public interface JcFundingPlanFourService extends IBaseService<JcFundingPlanFour>, ProxySelf<JcFundingPlanFourService>{
    List<JcFundingPlanFour> selectAll(IRequest iRequest,JcFundingPlanFour jcFundingPlanFour,int page,int pageSize);
}