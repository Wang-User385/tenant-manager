package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReq;

import javax.servlet.http.HttpSession;
import java.util.List;


public interface IConDebtExemptionReqService extends IBaseService<HlsCusConDebtExemptionReq>, ProxySelf<IConDebtExemptionReqService> {

    void hlsCusConDebtExemptionReqSave(HttpSession session, IRequest request, HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq);
    void hlsCusConDebtExemptionReqSave2(HttpSession session, IRequest request, HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq);
    HlsCusConDebtExemptionReq conDebtSubmitWfl(HttpSession session, IRequest iRequest, HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq);
    HlsCusConDebtExemptionReq conDebtSubmitWfl2(HttpSession session, IRequest iRequest, HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq, List<Long> contractId);

}