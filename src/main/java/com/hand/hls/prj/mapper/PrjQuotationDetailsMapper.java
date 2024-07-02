package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;

import java.util.List;

public interface PrjQuotationDetailsMapper<T extends HlsCusPrjQuotationDetails> extends Mapper<HlsCusPrjQuotationDetails> {
    List<HlsCusPrjQuotationDetails> queryDetailsById(HlsCusPrjQuotationDetails prjQuotationDetails);

    int deleteDetailsById(HlsCusPrjQuotation HlsCusPrjQuotation);
}
