package com.hand.hls.cap.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cap.dto.HlsCusCapFinancingPlan;
import com.hand.hls.cap.dto.HlsCusCapFinancingPlanLn;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface HlsCusCapFinancingPlanLnService extends IBaseService<HlsCusCapFinancingPlanLn>, ProxySelf<HlsCusCapFinancingPlanLnService> {


    /**
     * 更新状态
     * @param planId
     * @param financePlanLineId
     * @return
     */
    int updateFinancePlanStatus(Long planId, Long financePlanLineId);

    /**
     * 判断行信息是否被关联了
     * @param request 请求,暂时不用
     * @param dto 需要判断的IDS
     * @return 每个ID是否存在
     */
    Map<Long,Integer> haveRelevanced(IRequest request, List<HlsCusCapFinancingPlanLn> dto);

    /**
     * 作废单子
     * @param dto
     * @param request
     * @return
     */
    ResponseData cancelPlan(HlsCusCapFinancingPlan dto, HttpServletRequest request);
}
