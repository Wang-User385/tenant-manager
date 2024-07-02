package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjLeaseInfo;
import com.hand.hls.hn.dto.PrjLeaseMakeRule;

import java.util.List;

public interface PrjLeaseMakeRuleMapper extends Mapper<PrjLeaseMakeRule>{

    List<PrjLeaseMakeRule> selectPrjLeaseMakeRule(PrjLeaseMakeRule prjLeaseMakeRule);

}