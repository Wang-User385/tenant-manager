package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fp.dto.FundingExecute;
import com.hand.hls.fp.dto.FundingExecuteLn;

import java.util.List;

public interface FundingExecuteLnService extends IBaseService<FundingExecuteLn>, ProxySelf<FundingExecuteLnService>{
    List<FundingExecute> executeAmount(IRequest request, FundingExecute execute);
}