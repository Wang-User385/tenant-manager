package com.hand.hls.csh.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.csh.dto.CshPaymentAttachment;
import com.hand.hls.csh.service.ICshPaymentAttachmentService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class CshPaymentAttachmentServiceImpl extends BaseServiceImpl<CshPaymentAttachment> implements ICshPaymentAttachmentService{

}