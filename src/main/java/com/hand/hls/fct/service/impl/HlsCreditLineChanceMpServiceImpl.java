package com.hand.hls.fct.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.fct.dto.HlsCreditLineChanceMp;
import com.hand.hls.fct.service.IHlsCreditLineChanceMpService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditLineChanceMpServiceImpl extends BaseServiceImpl<HlsCreditLineChanceMp> implements IHlsCreditLineChanceMpService{

}