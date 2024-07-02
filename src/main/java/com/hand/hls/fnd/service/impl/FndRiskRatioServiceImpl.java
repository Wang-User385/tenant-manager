package com.hand.hls.fnd.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.fnd.dto.FndRiskRatio;
import com.hand.hls.fnd.service.IFndRiskRatioService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class FndRiskRatioServiceImpl extends BaseServiceImpl<FndRiskRatio> implements IFndRiskRatioService{

}