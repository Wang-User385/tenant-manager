package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjQuotationElements;

import java.util.List;

public interface HlsCusQuatationElementsMapper extends Mapper<HlsCusPrjQuotationElements>{

    List<HlsCusPrjQuotationElements> queryByQuatationId(HlsCusPrjQuotationElements prjQuatationElements);

    List<HlsCusPrjQuotationElements> queryElementInfo(HlsCusPrjQuotationElements prjQuatationElements);

    List<HlsCusPrjQuotationElements> selectQuotationElementRemoveInfo(HlsCusPrjQuotationElements elements);

    List<HlsCusPrjQuotationElements> selectQuotationElementDiffInfo(HlsCusPrjQuotationElements elements);

    List<HlsCusPrjQuotationElements> selectQuotationElementAddInfo(HlsCusPrjQuotationElements elements);

    List<HlsCusPrjQuotationElements> selectRentalRemoveInfo(HlsCusPrjQuotationElements elements);

    List<HlsCusPrjQuotationElements> selectRentalDiffInfo(HlsCusPrjQuotationElements elements);

    List<HlsCusPrjQuotationElements> selectRentalAddInfo(HlsCusPrjQuotationElements elements);
}