package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.ConfirmBatch;
import org.apache.ibatis.annotations.Param;

public interface ConfirmBatchMapper extends Mapper<ConfirmBatch>{
    ConfirmBatch queryMaxCountByDay();

    ConfirmBatch queryDealerMaxCountByDay();

    int batchDel(@Param("batchIds") String batchIds);
}