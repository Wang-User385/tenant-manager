package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ConContractChangeCashflow;
import org.apache.ibatis.annotations.Param;

public interface ConContractChangeCashflowMapper extends Mapper<ConContractChangeCashflow> {

    void deleteChangeCashflowByChangeReqId(@Param("changeReqId") Long changeReqId);
    Double getChangeReqIrrByChangeReqId(@Param("changeReqId") Long changeReqId);

    void dataTransfer(@Param("changeReqId")Long changeReqId);
}