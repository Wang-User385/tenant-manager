package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusLonContractRepaymentPlan;

import java.util.List;

public interface HlsCusLonContractRepaymentPlanMapper<T extends HlsCusLonContractRepaymentPlan> extends Mapper<HlsCusLonContractRepaymentPlan> {
    List<HlsCusLonContractRepaymentPlan> selectRepaymentPlan(HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlan);

    List<HlsCusLonContractRepaymentPlan> selectRepaymentPlanOrderByRepaymentDate(HlsCusLonContractRepaymentPlan hlsCusLonContractRepaymentPlan);
}