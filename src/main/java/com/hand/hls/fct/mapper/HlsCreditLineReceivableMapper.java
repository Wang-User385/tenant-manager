package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCreditLineReceivable;
import com.hand.hls.fct.dto.HlsCreditPlanLine;

import java.util.List;


public interface HlsCreditLineReceivableMapper extends Mapper<HlsCreditLineReceivable>{
    /***
     * 应收账款明细查询
     * @param hlsCreditLineReceivable
     * @return
     */
    List<HlsCreditLineReceivable> findFactoringInfo(HlsCreditLineReceivable hlsCreditLineReceivable);
}