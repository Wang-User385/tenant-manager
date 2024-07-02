package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpChangeReq;

import java.util.List;

public interface HlsBpChangeReqService extends IBaseService<HlsBpChangeReq>, ProxySelf<HlsBpChangeReqService>{

    List<HlsBpChangeReq> selectAll(IRequest iRequest, HlsBpChangeReq hlsBpChangeReq, int page, int pageSize);
}