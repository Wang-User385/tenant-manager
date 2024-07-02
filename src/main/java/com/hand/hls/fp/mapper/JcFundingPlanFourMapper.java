package com.hand.hls.fp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.JcFundingPlanFour;

import java.util.List;

public interface JcFundingPlanFourMapper extends Mapper<JcFundingPlanFour>{
    List<JcFundingPlanFour> queryAll(JcFundingPlanFour jcFundingPlanFour);
}