package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpMasterVisitRecord;

import java.util.List;

public interface HlsCusIBpMasterVisitRecordService extends IBaseService<HlsCusBpMasterVisitRecord>, ProxySelf<HlsCusIBpMasterVisitRecordService> {
    List<HlsCusBpMasterVisitRecord> selectByBpid(IRequest iRequest, HlsCusBpMasterVisitRecord dto, int page, int pageSize);
}