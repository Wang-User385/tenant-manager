package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.HlsCusDepositDeductionHd;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsCusDepositDeductionHdMapper extends Mapper<HlsCusDepositDeductionHd> {

    /**
     * 查询单个数据
     *
     * @param depositDeductionHdId
     * @return
     */
    HlsCusDepositDeductionHd selectHlsCusDepositDeductionData(@Param("depositDeductionHdId") Long depositDeductionHdId);


    /**
     * 查询汇总界面
     *
     * @param depositDeductionHd
     * @return
     */
    List<HlsCusDepositDeductionHd> selectHlsCusDepositHeaderData(HlsCusDepositDeductionHd depositDeductionHd);


    Map<String, Long> selectProjectUnitAndCompany(@Param("deductionDocCategory") String deductionDocCategory, @Param("contractId") Long contractId);

}