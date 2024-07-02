package com.hand.hls.hn.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.dto.PrjLeaseInspect;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import hls.core.utils.exception.HlsCusException;

public interface IPrjLeaseInspectService extends IBaseService<PrjLeaseInspect>{
    void submitPrjContractChange(IRequest request, PrjLeaseInspect prjLeaseInspect);

    //生成权限字符串
    String generateAuthorityString(IRequest iRequest);

    PrjLeaseInspect prjLeaseInspectCreate(IRequest requestCtx, PrjLeaseInspect dto) throws HlsCusException;
}