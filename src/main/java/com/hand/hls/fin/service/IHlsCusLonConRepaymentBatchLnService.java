package com.hand.hls.fin.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatchLn;

import java.util.List;

public interface IHlsCusLonConRepaymentBatchLnService extends IBaseService<HlsCusLonConRepaymentBatchLn>, ProxySelf<IHlsCusLonConRepaymentBatchLnService>{

    List<HlsCusLonConRepaymentBatchLn> queryWithdrawAccount(HlsCusLonConRepaymentBatchLn hlsCusLonConRepaymentBatchLn);
}