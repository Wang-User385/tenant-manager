package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsProductDefDealer;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsProductDefinitionMapper extends Mapper<HlsProductDefinition> {

    /**
     * 查询产品列表
     *
     * @param hlsProductDefinition
     * @return list
     */
    List<HlsProductDefinition> selectHlsProductDefinitionList(HlsProductDefinition hlsProductDefinition);

    List<Map> selectHlsProductDefinitionInfo(HlsProductDefinition hlsProductDefinition);

    List<HlsProductDefinition> queryProductDefinitionInfoByReplyId(Long replyId);

    List<HlsProductDefinition> queryDefinitionIdAndCreditLineIdByDefinitionCode(@Param("bpId") Long bpId, @Param("definitionCode")String definitionCode);

    String queryDefinitionCodeByPriceListAndBpId(@Param("bpId") Long bpId, @Param("priceList")String priceList);

    List<HlsProductDefinition> queryDefinitionByDefinitionCodeAndEnabledFlag(@Param("definitionCode")String definitionCode);
    List<HlsProductDefinition> queryProductDefDealerByDealerId(@Param("dealerId") Long dealerId);
}