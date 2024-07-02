package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusCtLonContractChangeReq;

import java.util.List;

public interface HlsCusCtLonContractChangeReqService extends IBaseService<HlsCusCtLonContractChangeReq>, ProxySelf<HlsCusCtLonContractChangeReqService> {
    List<HlsCusCtLonContractChangeReq> selectAllChangeReq(HlsCusCtLonContractChangeReq lonContractChangeReq, int page, int pageSize);

    HlsCusCtLonContractChangeReq selectLonConChangeReqChart(IRequest requestContext);

    void updateChangeReq(HlsCusCtLonContractChangeReq hlsCusCtLonContractChangeReq);
}