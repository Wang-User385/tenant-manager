package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsBpMasterInceptRule;
import com.hand.hls.prj.service.IHlsBpMasterInceptRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpMasterInceptRuleServiceImpl extends BaseServiceImpl<HlsBpMasterInceptRule> implements IHlsBpMasterInceptRuleService {

}