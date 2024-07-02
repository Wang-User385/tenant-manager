package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.gld.dto.HlsCusLonContractRepaymentMerge;

public interface IHlsCusLonContractRepaymentMergeService extends IBaseService<HlsCusLonContractRepaymentMerge>, ProxySelf<IHlsCusLonContractRepaymentMergeService>{

    /*------------融资提款计提---------*/
    void calcLonConWithdrawFinCostNew(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, String type);

}