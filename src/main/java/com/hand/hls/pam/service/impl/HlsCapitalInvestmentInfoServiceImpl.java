package com.hand.hls.pam.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.HlsCapitalInvestmentInfo;
import com.hand.hls.pam.service.IHlsCapitalInvestmentInfoService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCapitalInvestmentInfoServiceImpl extends BaseServiceImpl<HlsCapitalInvestmentInfo> implements IHlsCapitalInvestmentInfoService{

}