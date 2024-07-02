package com.hand.hls.archive.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.archive.dto.HlsCusArchiveApproval;
import com.hand.hls.exception.HlsCusException;

import java.util.List;

public interface HlsCusArchiveApprovalService extends IBaseService<HlsCusArchiveApproval>, ProxySelf<HlsCusArchiveApprovalService>{

    /**
     * 提交审批
     * @param iRequest
     * @param hlsCusArchiveApprovals
     */
    void approvalSubmit(IRequest iRequest, List<HlsCusArchiveApproval> hlsCusArchiveApprovals) throws HlsCusException;

}