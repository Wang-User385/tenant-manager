package com.hand.hls.hn.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.hn.dto.PrjCheckPlan;
import com.hand.hls.hn.service.IPrjCheckPlanService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PrjCheckPlanServiceImpl extends BaseServiceImpl<PrjCheckPlan> implements IPrjCheckPlanService{

}