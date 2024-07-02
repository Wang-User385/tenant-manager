package com.hand.hls.rpt.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.rpt.dto.RptOffBalanceSheet;

import java.util.List;

public interface RptOffBalanceSheetMapper extends Mapper<RptOffBalanceSheet>{
    void deleteByContractId(RptOffBalanceSheet sheet);

    int insertOffBalanceSheetBatch(List<RptOffBalanceSheet> rptOffBalanceSheetList);

    List<RptOffBalanceSheet>  offBalanceSheetInOutQuery(RptOffBalanceSheet rptOffBalanceSheet);

}