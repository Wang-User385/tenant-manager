package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.HlsCusPrjProjectInsure;
import com.hand.hls.utils.ResMessageException;

import java.util.List;

public interface HlsCusPrjProjectInsureService extends IBaseService<HlsCusPrjProjectInsure>, ProxySelf<HlsCusPrjProjectInsureService> {

    List<HlsCusPrjProjectInsure> queryContractInsureInfo(IRequest requestContext, HlsCusPrjProjectInsure hlsCusPrjProjectInsure, int pagenum, int pagesize);

    void insureApproveWfl(IRequest requestContext, HlsCusPrjProjectInsure dto)  throws ResMessageException;

}