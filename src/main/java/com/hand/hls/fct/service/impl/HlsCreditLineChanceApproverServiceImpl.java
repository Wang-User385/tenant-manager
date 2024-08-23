package com.hand.hls.fct.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.fct.dto.HlsCreditLineChanceApprover;
import com.hand.hls.fct.service.HlsICreditLineChanceApproverService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditLineChanceApproverServiceImpl extends BaseServiceImpl<HlsCreditLineChanceApprover> implements HlsICreditLineChanceApproverService{

}