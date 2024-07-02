package com.hand.hls.cont.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.cont.dto.ContractPaymentPlan;
import com.hand.hls.cont.service.IContractPaymentPlanService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ContractPaymentPlanServiceImpl extends BaseServiceImpl<ContractPaymentPlan> implements IContractPaymentPlanService{

}