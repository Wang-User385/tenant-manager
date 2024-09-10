package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCreditPlan;
import com.hand.hls.prj.dto.HlsCusPrjProjectParam;

import java.util.List;
import java.util.Map;

public interface HlsCreditPlanMapper extends Mapper<HlsCreditPlan> {

    List<HlsCreditPlan> queryCreditPlanInfo(HlsCreditPlan hlsCreditPlan);
    List<HlsCreditPlan> queryCreditPlanInfoByProject(HlsCreditPlan hlsCreditPlan);

    List<Map> queryCreditPlanAuditInfo(HlsCusPrjProjectParam prjQuotation);

    List<Map> queryPaymentCreditPlanAuditInfo(HlsCusPrjProjectParam prjQuotation);


    /***
     * 保理授信信息
     */
    List<HlsCreditPlan> findFactoringInfo(HlsCreditPlan hlsCreditPlan);
    List<HlsCreditPlan> selectHlsCreditPlanByChanceId(HlsCreditPlan hlsCreditPlan);


    /***
     * 保理项目审批授信信息
     */
    List<HlsCreditPlan> findFactoringApprovalInfo(HlsCreditPlan hlsCreditPlan);

}