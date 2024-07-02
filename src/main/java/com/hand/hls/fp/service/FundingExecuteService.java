package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fp.dto.FundingExecute;
import com.hand.hls.fp.dto.JcFundFilling;

import java.util.List;

public interface FundingExecuteService extends IBaseService<FundingExecute>, ProxySelf<FundingExecuteService>{
    List<FundingExecute> queryAllByUnit(IRequest iRequest, FundingExecute execute, int page, int pageSize);
}