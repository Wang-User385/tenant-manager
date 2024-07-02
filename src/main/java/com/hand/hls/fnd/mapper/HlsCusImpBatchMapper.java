package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsCusImpBatch;

import java.util.List;

public interface HlsCusImpBatchMapper extends Mapper<HlsCusImpBatch> {

    /**
     * 查询批次
     * @param dto
     * @return
     */
    List<HlsCusImpBatch> selectBatch(HlsCusImpBatch dto);

}