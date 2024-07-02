package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusDepositDeductionHd;

import java.util.List;
import java.util.Map;

public interface HlsCusDepositDeductionHdService extends IBaseService<HlsCusDepositDeductionHd>, ProxySelf<HlsCusDepositDeductionHdService> {


    List<HlsCusDepositDeductionHd> selectHlsCusDepositHeaderData(IRequest iRequest, HlsCusDepositDeductionHd depositDeductionHd, int page, int pageSize);


    HlsCusDepositDeductionHd selectHlsCusDepositDeductionData(IRequest iRequest, Long depositDeductionHdId);


    void deleteDepositDeductionHd(IRequest iRequest, HlsCusDepositDeductionHd depositDeductionHd);


    Map<String, Long> selectProjectUnitAndCompany(String deductionDocCategory, Long contractId);
}