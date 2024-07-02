package com.hand.hls.rw.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import hls.core.utils.exception.HlsCusException;

import java.util.List;
import java.util.Map;

public interface HlsCusIRiskWarningService extends IBaseService<HlsCusRiskWarning>, ProxySelf<HlsCusIRiskWarningService> {
    HlsCusRiskWarning submit(IRequest iRequest, HlsCusRiskWarning riskWarning);

    HlsCusRiskWarning submitWfl(IRequest iRequest, HlsCusRiskWarning riskWarning) throws HlsCusException;

    /**
     * 风险管理提交审批去除工作流
     * @param iRequest
     * @param riskWarning
     * @return
     * @throws HlsCusException
     */
    HlsCusRiskWarning submitWithoutWfl(IRequest iRequest, HlsCusRiskWarning riskWarning) throws HlsCusException;

    HlsCusRiskWarning releaseWarning(IRequest iRequest, HlsCusRiskWarning riskWarning);

    HlsCusRiskWarning submitChangeWfl(IRequest iRequest, HlsCusRiskWarning riskWarning);

    void copyChangeData(IRequest iRequest, HlsCusRiskWarning riskWarning);

    List<HlsCusRiskWarning> queryAll(IRequest iRequest, HlsCusRiskWarning riskWarning);

    List<HlsCusRiskWarning> homeThirdQuery(IRequest iRequest, HlsCusRiskWarning riskWarning, int page, int pageSize);

    List<HlsCusRiskWarning> selectIsReleasing(IRequest iRequest, HlsCusRiskWarning riskWarning);

    List<Map> selectHomeChart(IRequest iRequest, HlsCusRiskWarning riskWarning);

    /**
     * 解除调整 新建
     * @param iRequest
     * @param riskWarning
     * @return
     */
    HlsCusRiskWarning createChange(IRequest iRequest, HlsCusRiskWarning riskWarning);


    /**
     * 取消变更
     * @param iRequest
     * @param riskWarning
     * @return
     */
    HlsCusRiskWarning cancelChange(IRequest iRequest, HlsCusRiskWarning riskWarning);


    HlsCusRiskWarning submitChange(IRequest iRequest, HlsCusRiskWarning riskWarning);
    List<HlsCusRiskWarning> queryAllNew(IRequest iRequest, HlsCusRiskWarning riskWarning);

//    HlsCusRiskWarning updateRiskWarningBySky(IRequest iRequest,HlsCusRiskWarning riskWarning,String postData);
}
