package com.hand.hls.rw.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.dto.HlsCusRiskWarningInfo;

import java.util.List;

public interface HlsCusIRiskWarningInfoService extends IBaseService<HlsCusRiskWarningInfo>, ProxySelf<HlsCusIRiskWarningInfoService> {
    HlsCusRiskWarningInfo submit(IRequest iRequest, HlsCusRiskWarningInfo riskWarningInfo);

    List<HlsCusRiskWarningInfo> selectRiskWarningInfo(IRequest iRequest, HlsCusRiskWarningInfo riskWarningInfo);
    List<HlsCusRiskWarningInfo> queryAll (IRequest iRequest, HlsCusRiskWarningInfo warningInfo);
    List<HlsCusRiskWarningInfo> queryPrjCon (IRequest iRequest,HlsCusRiskWarningInfo riskWarning);
    void saveWarning(HlsCusRiskWarning  warning ,IRequest iRequest);
}