package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusContractRepaymentLn;

import java.util.List;

public interface HlsCusContractRepaymentLnService extends IBaseService<HlsCusContractRepaymentLn>, ProxySelf<HlsCusContractRepaymentLnService> {
    List<HlsCusContractRepaymentLn> selectData(IRequest request, HlsCusContractRepaymentLn hlsCusContractRepaymentLn, int page, int pagesize);

    HlsCusContractRepaymentLn selectRepaymentLnAmountSum(Long repaymentId);
}