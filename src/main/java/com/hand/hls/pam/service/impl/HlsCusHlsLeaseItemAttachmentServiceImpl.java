package com.hand.hls.pam.service.impl;


import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.pam.dto.HlsCusHlsLeaseItemAttachment;
import com.hand.hls.pam.service.HlsCusHlsLeaseItemAttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsLeaseItemAttachmentServiceImpl extends BaseServiceImpl<HlsCusHlsLeaseItemAttachment> implements HlsCusHlsLeaseItemAttachmentService {

}