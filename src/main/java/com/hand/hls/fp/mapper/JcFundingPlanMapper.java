package com.hand.hls.fp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.JcFundingPlan;

import java.util.List;

public interface JcFundingPlanMapper extends Mapper<JcFundingPlan>{

    List<JcFundingPlan> queryAll(JcFundingPlan jcFundingPlan);
    List<JcFundingPlan> queryUintCodeList(JcFundingPlan jcFundingPlan);
    List<JcFundingPlan> queryByReportCode(JcFundingPlan jcFundingPlan);
}