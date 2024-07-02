package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshAllocationReceipt;

import java.util.List;

public interface CshAllocationReceiptMapper extends Mapper<CshAllocationReceipt>{
    List<CshAllocationReceipt> receiptQuery(CshAllocationReceipt cshAllocationReceipt);
    List<CshAllocationReceipt> receiptQueryAll(CshAllocationReceipt cshAllocationReceipt);

}