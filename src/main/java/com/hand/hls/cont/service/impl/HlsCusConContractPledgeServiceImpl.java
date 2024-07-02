package com.hand.hls.cont.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractPledge;
import com.hand.hls.cont.service.HlsCusConContractPledgeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractPledgeServiceImpl extends BaseServiceImpl<HlsCusConContractPledge> implements HlsCusConContractPledgeService {

}