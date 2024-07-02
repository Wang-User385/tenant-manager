package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.PrjProjectApproval;
import com.hand.hls.prj.dto.ProjectApprovalCondition;
import com.hand.hls.utils.ResMessageException;

import java.util.List;

public interface IProjectApprovalService extends IBaseService<PrjProjectApproval>, ProxySelf<IProjectApprovalService> {

    ResponseData approvalSubmit(IRequest requestCtx, String approvalId, String meetingId, String meetingTime);

    void approvalWflSubmit(IRequest requestCtx, HlsCusPrjProject dto, int pagenum, int pagesize) throws ResMessageException;

    List<PrjProjectApproval> queryAll(IRequest var1, PrjProjectApproval var2, int var3, int var4);

    List<PrjProjectApproval> queryAllReply(IRequest var1, PrjProjectApproval var2, int var3, int var4);

}