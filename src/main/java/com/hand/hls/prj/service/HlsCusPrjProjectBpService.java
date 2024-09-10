package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;

import java.util.List;
import java.util.Map;

public interface HlsCusPrjProjectBpService extends IBaseService<HlsCusPrjProjectBp>, ProxySelf<HlsCusPrjProjectBpService> {

    List<HlsCusPrjProjectBp> prjProjectBpBeforeInfoQuery(IRequest iRequest, HlsCusPrjProjectBp hlsCusPrjProjectBp, int page, int pagesize);

    List<HlsCusPrjProjectBp> prjProjectBpInfoQuery(IRequest iRequest, HlsCusPrjProjectBp hlsCusPrjProjectBp, int page, int pagesize);

    public List<HlsCusPrjProjectBp> prjProjectBpQuery(HlsCusPrjProjectBp hlsCusPrjProjectBp, int page, int pagesize);

    List<HlsCusPrjProjectBp> createSerialNumber(IRequest iRequest, Long projectId, List<HlsCusPrjProjectBp> dtos, String roleType);

    List<HlsCusPrjProjectBp> conProjectBpUpdate(IRequest requestCtx, List<HlsCusPrjProjectBp> dto);

    int deleteByPrjBpIds(List<Long> prjBpIds,Long projectId);

    HlsCusPrjProjectBp selectByBpId(HlsCusPrjProjectBp hlsCusPrjProjectBp);

    List<Map> selectPledgeAndMortgagor(IRequest iRequest,HlsCusPrjProjectBp bp,int pagenum,int pagesize);
    List<HlsCusPrjProjectBp> selectBpByProjectIdOrderByBpCategory(HlsCusPrjProjectBp hlsCusPrjProjectBp);
    List<HlsCusPrjProjectBp> saveBpByChanceId(IRequest requestCt,HlsCusPrjProjectBp hlsCusPrjProjectBp) throws HlsCusException;

}