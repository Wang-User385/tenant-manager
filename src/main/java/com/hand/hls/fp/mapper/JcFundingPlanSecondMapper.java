package com.hand.hls.fp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.JcFundingPlanSecond;

import java.util.List;

public interface JcFundingPlanSecondMapper extends Mapper<JcFundingPlanSecond>{

    List<JcFundingPlanSecond> queryAll(JcFundingPlanSecond jcFundingPlanSecond);
}