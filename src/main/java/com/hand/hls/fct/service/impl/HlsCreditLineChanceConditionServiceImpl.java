package com.hand.hls.fct.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.fct.dto.HlsCreditLineChanceCondition;
import com.hand.hls.fct.service.HlsICreditLineChanceConditionService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditLineChanceConditionServiceImpl extends BaseServiceImpl<HlsCreditLineChanceCondition> implements HlsICreditLineChanceConditionService{

}