package com.hand.hls.hls.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.HlsDurationAttachment;
import com.hand.hls.hls.service.HlsDurationAttachmentService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsDurationAttachmentServiceImpl extends BaseServiceImpl<HlsDurationAttachment> implements HlsDurationAttachmentService{

}