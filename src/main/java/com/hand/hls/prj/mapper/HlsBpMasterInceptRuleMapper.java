package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsBpMasterInceptRule;

import java.util.List;

public interface HlsBpMasterInceptRuleMapper extends Mapper<HlsBpMasterInceptRule>{
    List<HlsBpMasterInceptRule> query(Long bpId);
}