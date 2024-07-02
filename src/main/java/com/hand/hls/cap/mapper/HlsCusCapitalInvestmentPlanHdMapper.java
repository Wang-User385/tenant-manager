package com.hand.hls.cap.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cap.dto.HlsCusCapitalInvestmentPlanHd;

import java.util.List;
import java.util.Map;

public interface HlsCusCapitalInvestmentPlanHdMapper extends Mapper<HlsCusCapitalInvestmentPlanHd> {
    List<HlsCusCapitalInvestmentPlanHd> queryDetails(Map var1);
}