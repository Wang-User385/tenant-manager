package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusLonContractRepaymentPlan;

import java.util.List;

public interface HlsCusLonContractRepaymentPlanService extends IBaseService<HlsCusLonContractRepaymentPlan>, ProxySelf<HlsCusLonContractRepaymentPlanService> {

    List<HlsCusLonContractRepaymentPlan> selectRepaymentPlan(IRequest iRequest, HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlan);

    void deleteLonChangeRepaymentPlan(IRequest request, List<HlsCusLonContractRepaymentPlan> hlsCusLonContractRepaymentPlan);
}