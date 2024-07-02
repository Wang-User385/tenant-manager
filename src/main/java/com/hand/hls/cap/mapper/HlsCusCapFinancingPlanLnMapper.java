package com.hand.hls.cap.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cap.dto.HlsCusCapFinancingPlanLn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusCapFinancingPlanLnMapper extends Mapper<HlsCusCapFinancingPlanLn> {
    List<HlsCusCapFinancingPlanLn> queryConFinLn(HlsCusCapFinancingPlanLn dto);

    List<HlsCusCapFinancingPlanLn> queryConLov(HlsCusCapFinancingPlanLn dto);

    List<HlsCusCapFinancingPlanLn> queryPlanFinLn(HlsCusCapFinancingPlanLn dto);


    /**
     * 更新状态
     *
     * @param financePlanLineId
     * @return
     */
    int updateFinancePlanStatus(@Param("planId") Long planId, @Param("financePlanLineId") Long financePlanLineId);

    /**
     * 查询数据条数
     * @param planLn
     * @return
     */
    int getCountNo(@Param("planLn") HlsCusCapFinancingPlanLn planLn);
}
