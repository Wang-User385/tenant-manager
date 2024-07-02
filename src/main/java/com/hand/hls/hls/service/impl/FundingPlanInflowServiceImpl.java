package com.hand.hls.hls.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.FundingPlanInflow;
import com.hand.hls.hls.service.IFundingPlanInflowService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class FundingPlanInflowServiceImpl extends BaseServiceImpl<FundingPlanInflow> implements IFundingPlanInflowService{

}