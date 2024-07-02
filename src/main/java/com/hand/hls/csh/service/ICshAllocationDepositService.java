package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshAllocationDeposit;

import java.util.List;

public interface ICshAllocationDepositService extends IBaseService<CshAllocationDeposit>, ProxySelf<ICshAllocationDepositService>{

    /**
     * 二期功能：查询核销为保证金详情
     * @param requestCtx
     * @param cshAllocationDeposit
     * @param pagenum
     * @param pagesize
     * @return
     */
    List<CshAllocationDeposit> getDepositList(IRequest requestCtx, CshAllocationDeposit cshAllocationDeposit, int pagenum, int pagesize);

}