package com.hand.hls.cont.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.cont.dto.HlsCusContractTariffInfo;
import com.hand.hls.cont.service.IHlsCusContractTariffInfoService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusContractTariffInfoServiceImpl extends BaseServiceImpl<HlsCusContractTariffInfo> implements IHlsCusContractTariffInfoService{

}