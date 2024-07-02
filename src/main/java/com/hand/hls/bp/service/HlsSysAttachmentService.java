package com.hand.hls.bp.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusSysAttachment;

import java.util.List;

public interface HlsSysAttachmentService extends IBaseService<HlsCusSysAttachment>, ProxySelf<HlsSysAttachmentService> {
    List<HlsCusSysAttachment> sysAttachmentQuery(HlsCusSysAttachment var1);
}
