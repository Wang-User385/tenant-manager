package com.hand.hls.gld.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.prj.dto.HlsCusPrjProject;

import java.util.List;

public interface GldFinanceIncomeDayMapper extends Mapper<GldFinanceIncomeDay>{
    List<GldFinanceIncomeDay> reportQuery(GldFinanceIncomeDay gldFinanceIncomeDayList);

    List<GldFinanceIncomeDay> queryPreLeaseInterestByContractId(HlsCusPrjProject hlsCusPrjProject);

   void deleteGldFinanceIncomeDayInterest(GldFinanceIncomeDay gldFinanceIncomeDayList);
   void deleteGldFinanceIncomeDayCost(GldFinanceIncomeDay gldFinanceIncomeDayList);

    int insertGldFinanceIncomeDayBatch(List<GldFinanceIncomeDay> gldFinanceIncomeDayList);
}