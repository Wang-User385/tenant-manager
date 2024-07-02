package com.hand.hls.cont.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.cont.dto.HlsCusContractTariffInfoLn;
import com.hand.hls.cont.service.IHlsCusContractTariffInfoLnService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusContractTariffInfoLnServiceImpl extends BaseServiceImpl<HlsCusContractTariffInfoLn> implements IHlsCusContractTariffInfoLnService{

}