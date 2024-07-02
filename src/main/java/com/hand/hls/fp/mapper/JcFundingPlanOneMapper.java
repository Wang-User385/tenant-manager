package com.hand.hls.fp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.JcFundingPlanOne;

import java.util.List;

public interface JcFundingPlanOneMapper extends Mapper<JcFundingPlanOne>{

    List<JcFundingPlanOne> queryAll(JcFundingPlanOne jcFundingPlanOne);
    List<JcFundingPlanOne> queryList(JcFundingPlanOne jcFundingPlanOne);
}