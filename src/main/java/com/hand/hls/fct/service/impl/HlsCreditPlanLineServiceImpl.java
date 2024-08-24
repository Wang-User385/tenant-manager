package com.hand.hls.fct.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCreditPlanLine;
import com.hand.hls.fct.service.HlsICreditPlanLineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditPlanLineServiceImpl extends BaseServiceImpl<HlsCreditPlanLine> implements HlsICreditPlanLineService {

}