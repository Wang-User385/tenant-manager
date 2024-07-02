package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshAllocation;

import java.util.List;

public interface CshAllocationMapper extends Mapper<CshAllocation>{
    List<CshAllocation>allocationQuery(CshAllocation cshAllocation);
}