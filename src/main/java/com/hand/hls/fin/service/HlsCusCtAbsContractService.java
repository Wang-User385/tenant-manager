package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusCtAbsContract;

import java.util.List;

public interface HlsCusCtAbsContractService extends IBaseService<HlsCusCtAbsContract>, ProxySelf<HlsCusCtAbsContractService> {
    List<HlsCusCtAbsContract> selectLonAbsContract(IRequest request, HlsCusCtAbsContract hlsCusCtAbsContract, int page, int pageSize);
}