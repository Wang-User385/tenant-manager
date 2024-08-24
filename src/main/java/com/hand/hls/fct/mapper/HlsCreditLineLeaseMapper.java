package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCreditLineLease;
import com.hand.hls.fct.dto.HlsCreditPlanLine;

import java.util.List;


public interface HlsCreditLineLeaseMapper extends Mapper<HlsCreditLineLease>{
    /***
     * 保理抵质押明细查询
     * @param hlsCreditLineLease
     * @return
     */
    List<HlsCreditLineLease> findFactoringInfo(HlsCreditLineLease hlsCreditLineLease);
}