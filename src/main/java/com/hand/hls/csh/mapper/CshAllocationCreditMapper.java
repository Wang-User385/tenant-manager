package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshAllocationCredit;

import java.util.List;

public interface CshAllocationCreditMapper extends Mapper<CshAllocationCredit> {
    List<CshAllocationCredit> creditQuery(CshAllocationCredit cshAllocationCredit);
}