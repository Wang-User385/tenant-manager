package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsProductStructure;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface HlsCusAbsProductStructureMapper extends Mapper<HlsCusAbsProductStructure> {


    /**
     * 查询分层机构
     * @param productStructure
     * @return
     */
    List<HlsCusAbsProductStructure> selectProductStructureData(HlsCusAbsProductStructure productStructure);


    /**
     * 兑付数据
     * @param structureId
     * @return
     */
    HlsCusAbsProductStructure selectCashStructure(@Param("structureId") Long structureId, @Param("dataClass") String dataClass, @Param("times") Long times);


    /**
     * 兑付利息总和
     * @param productId
     * @param interestPeriodDays
     * @return
     */
    BigDecimal selectStructureCashInterestSum(@Param("productId") Long productId, @Param("interestPeriodDays") Long interestPeriodDays);


    /**
     * 引用数量
     * @param productStructure
     * @return
     */
    int selectStructureQuoteCount(HlsCusAbsProductStructure productStructure);

}