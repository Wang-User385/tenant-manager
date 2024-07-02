package com.hand.hls.cont.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusContractTermination;

public interface HlsCusContractTerminationService extends IBaseService<HlsCusContractTermination>, ProxySelf<HlsCusContractTerminationService> {
    HlsCusContractTermination queryTerminationByContractId(Long contractId);
}