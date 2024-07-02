package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProductSubscribe;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface HlsCusAbsProductSubscribeMapper extends Mapper<HlsCusAbsProductSubscribe> {
    /**
     * 查询自持认购信息
     */
    List<HlsCusAbsProductSubscribe> queryProductSubscribe(HlsCusAbsProductSubscribe hlsCusAbsProductSubscribe);


    /**
     * 认购金额总和与发行金额差值
     * @param productId
     * @return
     */
    BigDecimal selectSubscribeAmountSum(@Param("productId") Long productId, @Param("structureId") Long structureId);


    /**
     * 未释放完全的数据
     * @param productId
     * @return
     */
    int selectSubscribeNotReleaseCount(@Param("productId") Long productId);
}
