package com.hand.hls.vat.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.vat.dto.ReceiptAttachment;
import com.hand.hls.vat.service.IReceiptAttachmentService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReceiptAttachmentServiceImpl extends BaseServiceImpl<ReceiptAttachment> implements IReceiptAttachmentService{

}