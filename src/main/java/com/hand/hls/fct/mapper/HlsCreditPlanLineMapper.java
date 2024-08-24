package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCreditPlanLine;

import java.util.List;


public interface HlsCreditPlanLineMapper extends Mapper<HlsCreditPlanLine>{

    /***
     * 保理方案明细查询
     * @param hlsCreditPlanLine
     * @return
     */
    List<HlsCreditPlanLine> findFactoringInfo(HlsCreditPlanLine hlsCreditPlanLine);
}