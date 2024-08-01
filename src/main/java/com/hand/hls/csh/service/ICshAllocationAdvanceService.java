package com.hand.hls.csh.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshAllocationAdvance;

import java.util.List;

public interface ICshAllocationAdvanceService extends IBaseService<CshAllocationAdvance>, ProxySelf<ICshAllocationAdvanceService>{

    //查询核销为预收款的数据
    List<CshAllocationAdvance> queryAllocationAdvance(CshAllocationAdvance cshAllocationAdvance,int page,int pagesize);

}