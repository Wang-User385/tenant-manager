package com.hand.hls.fp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.JcFundingPlanThree;

import java.util.List;

public interface JcFundingPlanThreeMapper extends Mapper<JcFundingPlanThree>{
    List<JcFundingPlanThree> queryAll(JcFundingPlanThree jcFundingPlanThree);
}