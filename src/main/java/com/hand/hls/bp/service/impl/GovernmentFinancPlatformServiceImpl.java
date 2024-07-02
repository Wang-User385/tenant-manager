package com.hand.hls.bp.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.GovernmentFinancPlatform;
import com.hand.hls.bp.service.IGovernmentFinancPlatformService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class GovernmentFinancPlatformServiceImpl extends BaseServiceImpl<GovernmentFinancPlatform> implements IGovernmentFinancPlatformService{

}