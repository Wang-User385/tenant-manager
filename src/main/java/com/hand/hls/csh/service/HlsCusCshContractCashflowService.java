package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshContractCashflow;

import java.util.List;
import java.util.Map;

public interface HlsCusCshContractCashflowService extends IBaseService<HlsCusCshContractCashflow>, ProxySelf<HlsCusCshContractCashflowService> {

    /**
     * 收付管理查询未完全核销的现金流主页面查询
     *
     * @param request
     * @param hlsCusCshContractCashflow
     * @param page
     * @param pageSize
     * @return
     */
    List<Map> selectNotFullContractCashflowHome(IRequest request, HlsCusCshContractCashflow hlsCusCshContractCashflow, int page, int pageSize);

    List<Map> selectAllContractCashflowHome(IRequest request, HlsCusCshContractCashflow hlsCusCshContractCashflow, int page, int pageSize,String sortName,String sortOrder);

    List<Map> selectRecoveryRate(IRequest request, HlsCusCshContractCashflow hlsCusCshContractCashflow, int page, int pageSize);

    List<Map> selectUnitRecoveryRate(IRequest request, HlsCusCshContractCashflow hlsCusCshContractCashflow, int page, int pageSize,String sortName,String sortOrder);

    List<HlsCusCshContractCashflow> selectUnitLov(IRequest request, HlsCusCshContractCashflow hlsCusCshContractCashflow, int page, int pageSize);

}

