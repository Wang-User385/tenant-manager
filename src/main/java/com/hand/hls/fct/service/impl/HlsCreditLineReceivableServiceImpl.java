package com.hand.hls.fct.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCreditLineReceivable;
import com.hand.hls.fct.service.HlsICreditLineReceivableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditLineReceivableServiceImpl extends BaseServiceImpl<HlsCreditLineReceivable> implements HlsICreditLineReceivableService {

}