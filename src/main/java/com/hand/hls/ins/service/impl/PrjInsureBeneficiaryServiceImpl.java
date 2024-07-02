package com.hand.hls.ins.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.ins.dto.PrjInsureBeneficiary;
import com.hand.hls.ins.service.PrjInsureBeneficiaryService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PrjInsureBeneficiaryServiceImpl extends BaseServiceImpl<PrjInsureBeneficiary> implements PrjInsureBeneficiaryService{

}