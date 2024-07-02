package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;

import java.util.List;

public interface HlsCusConFloatingRateReqService extends IBaseService<HlsCusConFloatingRateReq>, ProxySelf<HlsCusConFloatingRateReqService> {
    /*调息头查询方法*/
//    List<HlsCusConFloatingRateReq> queryHeadList(IRequest iRequest, HlsCusConFloatingRateReq dto, int page, int pageSize);

    void floatingRateWorkFlowStart(IRequest request, HlsCusConFloatingRateReq cusConFloatingRateReq);
}