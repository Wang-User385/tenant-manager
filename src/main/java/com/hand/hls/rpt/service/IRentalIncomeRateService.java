package com.hand.hls.rpt.service;


import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.rpt.dto.RentalIncomeRate;


import java.sql.SQLException;
import java.util.List;

public interface IRentalIncomeRateService extends IBaseService<RentalIncomeRate>, ProxySelf<IRentalIncomeRateService>{
    //明细页面查询
    List<RentalIncomeRate> queryRentalRateInfo(IRequest request, RentalIncomeRate RentalIncomeRate, int page, int pageSize);

    List<RentalIncomeRate> queryRentalIncomeRate(IRequest request, RentalIncomeRate RentalIncomeRate, int page, int pageSize);

    //租金回收导入
    void IRentalIncomeRateExcelImport(IRequest iRequest, Long hdId) throws ExcelException, SQLException;

    //租金回收率汇总
    List<RentalIncomeRate> rentalIncomeRateTotal(IRequest request, RentalIncomeRate RentalIncomeRate, int page, int pageSize);

    //导入查询
    List<RentalIncomeRate> rateExcelImportSelect(Long var1);
}