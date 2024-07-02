package com.hand.hls.common.service.impl;


import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;
import com.hand.hls.common.service.HlsCusHapInterfaceOutboundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHapInterfaceOutboundServiceImpl extends BaseServiceImpl<HlsCusHapInterfaceOutbound> implements HlsCusHapInterfaceOutboundService {



}