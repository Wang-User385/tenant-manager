package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflowHistory;

import java.util.List;
import java.util.Map;

public interface HlsCusPrjQuotationCashflowHistoryMapper extends Mapper<HlsCusPrjQuotationCashflowHistory>{

    List<Map> selectHistoryQuotationCashflowInfoQuery(Map map);

    List<Map> selectCalcPaynoteCashflow(Map map);

    List<Map> selectCalcQuotationPaymentCashflowInfoQuery(Map map);

}