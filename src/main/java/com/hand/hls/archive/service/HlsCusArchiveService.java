package com.hand.hls.archive.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.archive.dto.HlsCusArchive;
import hls.core.utils.exception.HlsCusException;

import java.util.List;

public interface HlsCusArchiveService extends IBaseService<HlsCusArchive>, ProxySelf<HlsCusArchiveService>{

    /**
     * 档案归集
     * @param iRequest
     * @param hlsCusArchive
     * @return
     * @throws HlsCusException
     */
    List<HlsCusArchive> archivePooling(IRequest iRequest,HlsCusArchive hlsCusArchive) throws HlsCusException;

    /**
     * 审批通过
     * @param iRequest
     * @param projectId
     */
    void approvedWfl(IRequest iRequest, Long projectId) throws com.hand.hls.exception.HlsCusException;

    /**
     * 归档确认
     */
    void archiveConfirm(IRequest iRequest,List<HlsCusArchive> dto);
}