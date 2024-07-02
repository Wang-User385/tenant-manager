package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpSeniorPersion;
import hls.core.utils.exception.HlsCusException;

import java.util.List;

public interface HlsCusBpSeniorPersionService extends IBaseService<HlsCusBpSeniorPersion>, ProxySelf<HlsCusBpSeniorPersionService> {
    /*高管及主要人员查询*/
    List<HlsCusBpSeniorPersion> querySeniorByBpId(HlsCusBpSeniorPersion hlsCusBpSeniorPersion, IRequest requestContext, int page, int pagesize);

    int delectSeniorByBpId(List<HlsCusBpSeniorPersion> dtos) throws HlsCusException;
}