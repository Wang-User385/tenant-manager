package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusLonContractWithdrawPlan;

import java.util.List;

public interface HlsCusLonContractWithdrawPlanService extends IBaseService<HlsCusLonContractWithdrawPlan>, ProxySelf<HlsCusLonContractWithdrawPlanService> {
     void batchDeleteWithdrawPlan(IRequest iRequest, List<HlsCusLonContractWithdrawPlan> dto);

     void calcRepaymentPlan(IRequest iRequest, HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlan);

     void updateAllPrincipalOutStd(IRequest iRequest, HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlan);
}