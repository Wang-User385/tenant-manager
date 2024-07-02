package com.hand.hls.cap.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cap.dto.HlsCusCapitalInvestmentPlanLn;
import com.hand.hls.cap.service.HlsCusCapitalInvestmentPlanLnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCapitalInvestmentPlanLnServiceImpl extends BaseServiceImpl<HlsCusCapitalInvestmentPlanLn> implements HlsCusCapitalInvestmentPlanLnService {


}