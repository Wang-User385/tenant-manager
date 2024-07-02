package com.hand.hls.hn.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.FundingPlan;
import com.hand.hls.hls.dto.HlsCusFundingPlan;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.hn.dto.CheckPlanConContract;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.dto.PrjLeaseInspect;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import leaf.service.validation.ParameterNullException;

import java.util.List;

public interface IPrjCheckService extends IBaseService<PrjCheck>{
    void submitPrjContractChange(IRequest request, PrjCheck prjCheck) throws HlsCusException;

    void submitPrjCheckLs(IRequest request, PrjCheck prjCheck) throws HlsCusException;

    List<PrjCheck> queryContract(PrjCheck prjCheck);
    //生成权限字符串
    String generateAuthorityString(IRequest iRequest);

    PrjCheck prjCheckCreate(IRequest requestCtx, PrjCheck dto) throws HlsCusException;
}