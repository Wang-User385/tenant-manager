package com.hand.hls.rpt.mapper;



import com.hand.hap.mybatis.common.Mapper;

import com.hand.hls.rpt.dto.RentalIncomeRate;

import java.util.List;

public interface RentalIncomeRateMapper extends Mapper<RentalIncomeRate>{

    //明细页面查询
    List<RentalIncomeRate> queryRentalRateInfo(RentalIncomeRate RentalIncomeRate);

    List<RentalIncomeRate> queryRentalIncomeRate(RentalIncomeRate RentalIncomeRate);
    //租金回收率汇总查询
    List<RentalIncomeRate> rentalIncomeRateTotal(RentalIncomeRate RentalIncomeRate);

    List<RentalIncomeRate> rateExcelImportSelect(Long var1);

}