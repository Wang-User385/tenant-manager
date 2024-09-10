package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCreditPlan;

public interface HlsCreditPlanService extends IBaseService<HlsCreditPlan>, ProxySelf<HlsCreditPlanService> {
    void updateHlsCreditPlan(IRequest iRequest, Long chanceId, Long creditPlanId);

    void selectPlanByProject(IRequest iRequest, Long chanceId, Long creditPlanId);
}
