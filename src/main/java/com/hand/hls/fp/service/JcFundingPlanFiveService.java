package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fp.dto.JcFundingPlanFive;

import java.util.List;

public interface JcFundingPlanFiveService extends IBaseService<JcFundingPlanFive>, ProxySelf<JcFundingPlanFiveService>{

    List<JcFundingPlanFive> selectAll(IRequest iRequest, JcFundingPlanFive jcFundingPlanFive, int page, int pageSize);

}