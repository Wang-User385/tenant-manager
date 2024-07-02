package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;

public interface IHlsCusCshPaymentReqLnService extends IBaseService<HlsCusCshPaymentReqLn>, ProxySelf<CshWriteOffService> {
    void updateSourceDocLineIdByReqPaymentId(IRequest request, Long paymentReqId, Long cashflowId);
}
