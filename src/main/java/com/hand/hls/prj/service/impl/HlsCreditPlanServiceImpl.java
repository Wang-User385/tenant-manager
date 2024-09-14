package com.hand.hls.prj.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCreditPlan;
import com.hand.hls.prj.mapper.HlsCreditPlanMapper;
import com.hand.hls.prj.service.HlsCreditPlanService;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditPlanServiceImpl extends BaseServiceImpl<HlsCreditPlan>  implements HlsCreditPlanService {
    @Autowired
    private HlsCreditPlanMapper hlsCreditPlanMapper;
    @Override
    public void updateHlsCreditPlan(IRequest iRequest, Long chanceId, Long creditPlanId) {
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setSourceDocumentId(chanceId);
        hlsCreditPlan.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        hlsCreditPlan.setCreditPlanId(creditPlanId);
        hlsCreditPlanMapper.updateByPrimaryKeySelective(hlsCreditPlan);

    }

    @Override
    public void selectPlanByProject(IRequest iRequest, Long chanceId, Long creditPlanId) {


    }

    @Override
    public HlsCreditPlanService self() {
        return HlsCreditPlanService.super.self();
    }
}
