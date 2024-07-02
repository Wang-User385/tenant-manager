package com.hand.hls.cap.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cap.dto.HlsCusCapFinancingPlan;
import com.hand.hls.fin.dto.HlsCusLonContractWithdrawPlan;

import java.util.List;
import java.util.Map;

public interface HlsCusCapFinancingPlanMapper extends Mapper<HlsCusCapFinancingPlan> {
    /**
     * 融资计划首页GRID查询
     */
    List<HlsCusCapFinancingPlan> queryFinancingPlan(Map<String, Object> map);

    /**
     * 融资计划首页PIT图
     */
    List<HlsCusCapFinancingPlan> queryFinancingPlanChart(HlsCusCapFinancingPlan dto);
    /**
     * 融资计划首页PIT图(9月17日需求变更之后,按照融资状态进行分类)
     */
    List<HlsCusCapFinancingPlan> queryFinancingPlanChartNew(HlsCusCapFinancingPlan dto);

    /**
     * 融资计划首页GRID查询
     */
    List<HlsCusLonContractWithdrawPlan> queryLonFinancingPlan(HlsCusCapFinancingPlan dto);

}
