package com.hand.hls.GENER.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.GENER.dto.HlsGeneralIssue;
import com.hand.hls.utils.ResMessageException;

public interface IHlsGeneralIssueService extends IBaseService<HlsGeneralIssue>, ProxySelf<IHlsGeneralIssueService>{
    void SubmitWfl(IRequest iRequest, HlsGeneralIssue hlsgeneralissue) throws ResMessageException;

}