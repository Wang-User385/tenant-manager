package com.hand.hls.archive.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.archive.dto.HlsCusArchiveAttachment;
import hls.core.utils.exception.HlsCusException;

import java.util.List;

public interface HlsCusArchiveAttachmentService extends IBaseService<HlsCusArchiveAttachment>, ProxySelf<HlsCusArchiveAttachmentService>{

    /**
     * 档案整理
     * @param iRequest
     * @param list
     */
    void archiveArrangement(IRequest iRequest, List<HlsCusArchiveAttachment> list) throws HlsCusException;
}