package com.hand.hls.fct.mapper;

import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusHlsCreditLineMapper extends HlsCreditLineMapper<HlsCusHlsCreditLine> {

    /*授信首页chart查询*/
    List<HlsCusHlsCreditLine> FctCreditLineHomeChart(HlsCusHlsCreditLine hlsCusHlsCreditLine);

    List<HlsCusHlsCreditLine> selectHlsCreditByBpId(@Param("bpId") Long bpId);
    Double selectHlsCreditApplyAmountByBpId(@Param("bpId") Long bpId, @Param("groupFlag") String groupFlag);
    Double selectHlsCreditWriteOffAmountByBpId(@Param("bpId") Long bpId, @Param("groupFlag") String groupFlag);
    Double selectHlsCreditPreemptionAmtByBpId(@Param("bpId") Long bpId, @Param("groupFlag") String groupFlag);

    /**
     * 更新提款余额
     * @param creditLineId
     * @return
     */
    int updateWithdrawBalance(@Param("creditLineId") Long creditLineId);


    /**
     * 更新行已用金额
     * @param creditLineId
     * @return
     */
    int updateCreditExposureAmt(@Param("creditLineId") Long creditLineId);
}