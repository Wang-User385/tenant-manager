package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusHlsCreditLinePledge;

import java.util.List;

public interface HlsCusHlsCreditLinePledgeService extends IBaseService<HlsCusHlsCreditLinePledge>, ProxySelf<HlsCusHlsCreditLinePledgeService> {

    List<HlsCusHlsCreditLinePledge> queryCreditLinePledge(IRequest iRequest, HlsCusHlsCreditLinePledge hlsCusHlsCreditLinePledge, int page, int pageSize);

}
