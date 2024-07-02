package com.hand.hls.pam.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.HlsLeaseBuildDeliverInfo;
import com.hand.hls.pam.service.IHlsLeaseBuildDeliverInfoService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsLeaseBuildDeliverInfoServiceImpl extends BaseServiceImpl<HlsLeaseBuildDeliverInfo> implements IHlsLeaseBuildDeliverInfoService{

}