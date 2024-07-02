package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusContractRepaymentLn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusContractRepaymentLnMapper extends Mapper<HlsCusContractRepaymentLn> {
    List<HlsCusContractRepaymentLn> selectData(HlsCusContractRepaymentLn hlsCusContractRepaymentLn);

    HlsCusContractRepaymentLn selectRepaymentLnAmountSum(@Param("repaymentId") Long repaymentId);
}