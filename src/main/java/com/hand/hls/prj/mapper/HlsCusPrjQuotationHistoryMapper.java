package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotationHistory;

import java.util.List;
import java.util.Map;

public interface HlsCusPrjQuotationHistoryMapper extends Mapper<HlsCusPrjQuotationHistory>{
    Long selectVersionCount(HlsCusPrjProject hlsCusPrjProject);

    Long selectVersionCountCon(HlsCusConContract hlsCusConContract);

    List<HlsCusPrjQuotationHistory> queryQuotationHistoryByQuotationId(HlsCusPrjQuotationHistory quotation);

    List<HlsCusPrjQuotationHistory> ConFinancialXIRRHistoryQuery(Map map);
}