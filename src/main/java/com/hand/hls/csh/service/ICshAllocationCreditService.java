package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshAllocationCredit;
import com.hand.hls.csh.dto.CshAllocationReceipt;

import java.util.List;

public interface ICshAllocationCreditService extends IBaseService<CshAllocationCredit>, ProxySelf<ICshAllocationCreditService>{
    List<CshAllocationCredit> creditQuery(IRequest iRequest, CshAllocationCredit cshAllocationCredit, int page, int pageSize);
}