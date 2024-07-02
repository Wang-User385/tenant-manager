package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusCreditContract;
import com.hand.hls.fin.exception.AmoutOverdueException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface HlsCusICreditContractService extends IBaseService<HlsCusCreditContract>, ProxySelf<HlsCusICreditContractService> {
    HlsCusCreditContract save(IRequest iRequest, HlsCusCreditContract creditContract) throws AmoutOverdueException;
    List<HlsCusCreditContract> unitSelect(HlsCusCreditContract creditContract, int page, int pageSize);

    /**
     * 更新授信头上已授信金额
     * @param creditLineId
     * @return
     */
    int updateCreditContractExposureAmt(Long creditContractId,Long creditLineId);

    /**
     * 更新授信头上未占用金额
     * @param creditLineId
     * @return
     */
    int updateCreditContractUnExposureAmt(Long creditContractId,Long creditLineId);

}