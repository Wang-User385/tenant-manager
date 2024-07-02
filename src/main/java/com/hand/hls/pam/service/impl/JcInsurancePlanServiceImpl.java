package com.hand.hls.pam.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.JcInsurancePlan;
import com.hand.hls.pam.service.IJcInsurancePlanService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcInsurancePlanServiceImpl extends BaseServiceImpl<JcInsurancePlan> implements IJcInsurancePlanService{

}