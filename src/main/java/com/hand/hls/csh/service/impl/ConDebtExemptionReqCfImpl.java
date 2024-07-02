package com.hand.hls.csh.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReqCf;
import com.hand.hls.csh.service.IConDebtExemptionReqCfService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class ConDebtExemptionReqCfImpl extends BaseServiceImpl<HlsCusConDebtExemptionReqCf> implements IConDebtExemptionReqCfService {

}