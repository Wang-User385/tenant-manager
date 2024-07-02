package com.hand.hls.risk.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.risk.dto.RiskAttachment;
import com.hand.hls.risk.service.IRiskAttachmentService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class RiskAttachmentServiceImpl extends BaseServiceImpl<RiskAttachment> implements IRiskAttachmentService{

}