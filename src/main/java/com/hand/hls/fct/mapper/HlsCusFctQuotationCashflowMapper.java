package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusFctQuotationCashflowMapper extends Mapper<HlsCusFctQuotationCashflow> {

    List<HlsCusFctQuotationCashflow> selectNoticePrint(HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow);

    List<HlsCusFctQuotationCashflow>  selectFctContractTimesBpInfo(@Param("cashflowList") List<String> cashflowIdList);

}
