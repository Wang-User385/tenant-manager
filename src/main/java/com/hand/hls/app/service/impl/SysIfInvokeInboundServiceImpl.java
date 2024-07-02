package com.hand.hls.app.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.app.dto.SysIfInvokeInboundDto;
import com.hand.hls.app.service.SysIfInvokeInboundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class SysIfInvokeInboundServiceImpl extends BaseServiceImpl<SysIfInvokeInboundDto> implements SysIfInvokeInboundService {
}
