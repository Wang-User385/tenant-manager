package com.hand.hls.cap.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cap.dto.HlsCusCapitalInvestmentPlanLn;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsCusCapitalInvestmentPlanLnMapper extends Mapper<HlsCusCapitalInvestmentPlanLn> {

    List<HlsCusCapitalInvestmentPlanLn> planLnQuery(Map var1);;
    /**
     * 更新投放金额和状态
     * @param planLineId
     * @return
     */
    int  updateInvestmentAmount(@Param("planLineId") Long planLineId);

    /**
     * 更新总投放金额状态
     * @param planHeadId
     * @param planLineId
     * @return
     */
    int updateInvestmentAmountSum(@Param("planHeadId") Long planHeadId,@Param("planLineId") Long planLineId);
}
