package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.prj.dto.HlsCusPrjProject;

import java.util.List;

public interface IGldFinanceIncomeDayService extends IBaseService<GldFinanceIncomeDay>, ProxySelf<IGldFinanceIncomeDayService> {
    List<GldFinanceIncomeDay> reportQuery(IRequest iRequest,GldFinanceIncomeDay gldFinanceIncomeDay,int pagenum , int pagesize);

    List<GldFinanceIncomeDay> queryPreLeaseInterestByContractId(HlsCusPrjProject hlsCusPrjProject);
}