package com.hand.hls.fp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.JcFundingPlanFive;

import java.util.List;

public interface JcFundingPlanFiveMapper extends Mapper<JcFundingPlanFive>{

    List<JcFundingPlanFive> queryAll(JcFundingPlanFive jcFundingPlanFive);
}