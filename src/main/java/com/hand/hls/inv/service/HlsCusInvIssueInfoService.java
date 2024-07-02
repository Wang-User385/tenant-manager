package com.hand.hls.inv.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.inv.dto.HlsCusInvIssueInfo;

import java.util.List;

public interface HlsCusInvIssueInfoService extends IBaseService<HlsCusInvIssueInfo>, ProxySelf<HlsCusInvIssueInfoService> {

    List<HlsCusInvIssueInfo> issueInfoSave(IRequest iRequest, List<HlsCusInvIssueInfo> hlsCusInvIssueInfos);

}