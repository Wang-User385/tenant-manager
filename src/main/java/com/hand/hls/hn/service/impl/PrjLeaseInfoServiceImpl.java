package com.hand.hls.hn.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.hn.dto.PrjLeaseInfo;
import com.hand.hls.hn.service.IPrjLeaseInfoService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PrjLeaseInfoServiceImpl extends BaseServiceImpl<PrjLeaseInfo> implements IPrjLeaseInfoService{

}