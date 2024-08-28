package com.hand.hls.prj.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCreditPlan;
import com.hand.hls.prj.service.HlsCreditPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditPlanServiceImpl extends BaseServiceImpl<HlsCreditPlan>  implements HlsCreditPlanService {
    @Override
    public void updateHlsCreditPlan(IRequest iRequest, Long chanceId, Long creditPlanId) {
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setSourceDocumentId(chanceId);
        hlsCreditPlan.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        hlsCreditPlan.setCreditPlanId(creditPlanId);
        self().updateByPrimaryKeySelective(iRequest,hlsCreditPlan);

    }

    @Override
    public HlsCreditPlanService self() {
        return HlsCreditPlanService.super.self();
    }
}
