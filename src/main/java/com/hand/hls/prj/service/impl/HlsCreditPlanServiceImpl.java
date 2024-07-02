package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCreditPlan;
import com.hand.hls.prj.service.HlsCreditPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditPlanServiceImpl extends BaseServiceImpl<HlsCreditPlan>  implements HlsCreditPlanService {
}
