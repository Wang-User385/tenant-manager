package com.hand.hls.abs.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.abs.dto.HlsCusAbsContractFinish;

import java.util.List;

public interface HlsCusAbsContractFinishService extends IBaseService<HlsCusAbsContractFinish>, ProxySelf<HlsCusAbsContractFinishService> {

    /**
     * 提前结清表格查询
     */
    List<HlsCusAbsContractFinish> queryAbsContractFinish(IRequest request, HlsCusAbsContractFinish hlsCusAbsContractFinish, int page, int pageSize);
}
