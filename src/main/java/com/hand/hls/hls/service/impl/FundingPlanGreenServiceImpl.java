package com.hand.hls.hls.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.hls.dto.HlsCusFundingPlanGreen;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.FundingPlanGreen;
import com.hand.hls.hls.service.IFundingPlanGreenService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class FundingPlanGreenServiceImpl extends BaseServiceImpl<HlsCusFundingPlanGreen> implements IFundingPlanGreenService{

}