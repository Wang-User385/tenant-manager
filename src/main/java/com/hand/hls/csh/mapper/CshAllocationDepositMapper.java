package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshAllocationDeposit;

import java.util.List;

public interface CshAllocationDepositMapper extends Mapper<CshAllocationDeposit>{

    /**
     * 二期功能：查询核销为保证金详情
     * @param cshAllocationDeposit
     * @return
     */
    List<CshAllocationDeposit> getDepositList(CshAllocationDeposit cshAllocationDeposit);
}