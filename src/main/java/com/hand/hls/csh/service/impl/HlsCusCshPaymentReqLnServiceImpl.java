package com.hand.hls.csh.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;
import com.hand.hls.csh.service.IHlsCusCshPaymentReqLnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HlsCusCshPaymentReqLnServiceImpl extends BaseServiceImpl<HlsCusCshPaymentReqLn> implements IHlsCusCshPaymentReqLnService {

    @Autowired
    private HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;

    @Override
    public void updateSourceDocLineIdByReqPaymentId(IRequest request, Long paymentReqId, Long cashflowId) {
        hlsCusCshPaymentReqLnMapper.updateSourceDocLineIdByReqPaymentId(paymentReqId,cashflowId);
    }
}
