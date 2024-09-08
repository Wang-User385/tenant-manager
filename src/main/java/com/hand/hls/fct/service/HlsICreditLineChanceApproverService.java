package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCreditLineChanceApprover;
import com.hand.hls.utils.ResMessageException;

public interface HlsICreditLineChanceApproverService extends IBaseService<HlsCreditLineChanceApprover>, ProxySelf<HlsICreditLineChanceApproverService>{

    void saveVote(IRequest requestCtx, HlsCreditLineChanceApprover approver) throws ResMessageException;
}