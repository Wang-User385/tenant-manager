package com.hand.hls.cap.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cap.dto.HlsCusCapitalInvestmentPlanHd;

import java.util.List;

public interface HlsCusCapitalInvestmentPlanHdService extends IBaseService<HlsCusCapitalInvestmentPlanHd>, ProxySelf<HlsCusCapitalInvestmentPlanHdService> {
    HlsCusCapitalInvestmentPlanHd capPlanSubmitWfl(IRequest request, List<HlsCusCapitalInvestmentPlanHd> hlsCusCapitalInvestmentPlanHds);
}