package com.hand.hls.fin.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusLonContractRepaymentPlan;
import com.hand.hls.fin.service.HlsCusLonContractRepaymentPlanService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractRepaymentPlanServiceImpl extends BaseServiceImpl<HlsCusLonContractRepaymentPlan> implements HlsCusLonContractRepaymentPlanService {


    @Override
    public List<HlsCusLonContractRepaymentPlan> selectRepaymentPlan(IRequest iRequest, HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlan) {
        return null;
    }

    @Override
    public void deleteLonChangeRepaymentPlan(IRequest request, List<HlsCusLonContractRepaymentPlan> hlsCusLonContractRepaymentPlan) {

    }
}
