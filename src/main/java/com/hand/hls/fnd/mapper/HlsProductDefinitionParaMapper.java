package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.dto.HlsProductDefinitionPara;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsProductDefinitionParaMapper extends Mapper<HlsProductDefinitionPara> {

    /**
     * 查询产品参数列表
     *
     * @param hlsProductDefinition
     * @return list
     */
    List<HlsProductDefinitionPara> selectHlsProductDefinitionParaList(HlsProductDefinition hlsProductDefinition);

    /**
     * 根据报价查询产品参数列表
     *
     * @param hlsProductDefinitionPara
     * @return list
     */
    List<Map> selectHlsProductDefinitionParaByQuotation(HlsProductDefinitionPara hlsProductDefinitionPara);

    /**
     * 根据报价查询产品参数列表中的IRR
     *
     * @param quotationId
     * @return list
     */
    List<Map> selectHlsProductDefinitionParaIrr(@Param("quotationId") Long quotationId);

    Map selectHlsProductDefinitionByQuotation(HlsProductDefinitionPara hlsProductDefinitionPara);

    List<Map> selectHlsProductDefinitionParaByQuotationAddIrr(HlsProductDefinitionPara hlsProductDefinitionPara);

    List<HlsProductDefinitionPara> queryHlsProductDefinitionParaList(@Param("definitionId") Long definitionId,
                                                                     @Param("productPara") String productPara);
    Double getDefaulValue(@Param("definitionId") Long definitionId,@Param("productPara") String productPara);
}