package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.CshAllocation;
import com.hand.hls.csh.dto.CshAllocationReceipt;

import java.util.List;

public interface ICshAllocationReceiptService extends IBaseService<CshAllocationReceipt>, ProxySelf<ICshAllocationReceiptService>{
    List<CshAllocationReceipt> receiptQuery(IRequest iRequest, CshAllocationReceipt cshAllocationReceipt, int page, int pageSize);
}