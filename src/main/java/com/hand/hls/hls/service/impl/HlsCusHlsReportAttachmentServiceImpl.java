package com.hand.hls.hls.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.hls.dto.HlsCusHlsReportAttachment;
import com.hand.hls.hls.service.HlsCusHlsReportAttachmentService;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsReportAttachmentServiceImpl extends BaseServiceImpl<HlsCusHlsReportAttachment> implements HlsCusHlsReportAttachmentService {

}