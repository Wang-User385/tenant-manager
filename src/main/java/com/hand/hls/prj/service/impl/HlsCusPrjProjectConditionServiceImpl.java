package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjProjectCondition;
import com.hand.hls.prj.service.HlsCusPrjIProjectConditionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectConditionServiceImpl extends BaseServiceImpl<HlsCusPrjProjectCondition> implements HlsCusPrjIProjectConditionService {

}