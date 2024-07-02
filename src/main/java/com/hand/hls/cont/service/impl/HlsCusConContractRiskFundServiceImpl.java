package com.hand.hls.cont.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractRiskFund;
import com.hand.hls.cont.service.HlsCusConContractRiskFundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractRiskFundServiceImpl extends BaseServiceImpl<HlsCusConContractRiskFund> implements HlsCusConContractRiskFundService {

}
