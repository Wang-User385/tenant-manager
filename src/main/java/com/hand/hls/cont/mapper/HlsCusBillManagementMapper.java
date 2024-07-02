package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusBillManagement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusBillManagementMapper extends Mapper<HlsCusBillManagement> {
    List<HlsCusBillManagement> queryBillManagementList(HlsCusBillManagement hlsCusBillManagement);

    Long queryBillManagementTempSeq();

    List<HlsCusBillManagement> queryBillManagementListByBatchId(@Param("batchId") Long batchId);

    List<HlsCusBillManagement> queryBillManagementAttachment(HlsCusBillManagement dto);
}