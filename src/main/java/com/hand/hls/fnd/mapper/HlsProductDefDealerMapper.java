package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsProductDefDealer;
import org.apache.ibatis.annotations.Param;

public interface HlsProductDefDealerMapper extends Mapper<HlsProductDefDealer> {
    void deleteProductDefDealerByDefinitionId(@Param("definitionId") Long definitionId);

    Integer selectProductNumByBpName(@Param("replyProductId") Long replyProductId);
}