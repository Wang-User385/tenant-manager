package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshAllocationAdvance;

import java.util.List;

public interface CshAllocationAdvanceMapper extends Mapper<CshAllocationAdvance> {

    List<CshAllocationAdvance> queryAllocationAdvance(CshAllocationAdvance cshAllocationAdvance);

}