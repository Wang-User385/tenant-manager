package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshAllocation;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;

import java.text.ParseException;
import java.util.List;

public interface ICshAllocationService extends IBaseService<CshAllocation>, ProxySelf<ICshAllocationService>{
    List<CshAllocation> allocationQuery(IRequest iRequest,CshAllocation cshAllocation,int page,int pageSize,String sortName,String sortOrder);

    //自动匹配
    List<CshAllocation> autoAllocation(IRequest iRequest,String transactionIdStr) throws ResMessageException, ParseException, HlsCusException;

}