package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProductCashDetail;
import com.hand.hls.abs.dto.HlsCusAbsProductCollection;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface HlsCusAbsProductCashDetailMapper extends Mapper<HlsCusAbsProductCashDetail> {


    /**
     * 查询
     * @param productCashDetail
     * @return
     */
    List<HlsCusAbsProductCashDetail> selectProductCashDetailData(HlsCusAbsProductCashDetail productCashDetail);


    /**
     * 删除product下的数据
     * @param productId
     * @return
     */
    int deleteCashDetailByProduct(@Param("productId") Long productId, @Param("dataClass") String dataClass);


    /**
     * 兑付总本金
     * @param productId
     * @return
     */
    BigDecimal selectCashPrincipalSum(@Param("productId") Long productId, @Param("times") Long times);

    /**
     * 剩余本金
     *
     * @param productId
     * @return
     */
    BigDecimal queryRemainingPrincipalSum(@Param("productId") Long productId, @Param("collectionId") Long collectionId);

    void deleteCashDetailByCollection(HlsCusAbsProductCollection hlsCusAbsProductCollection);

}