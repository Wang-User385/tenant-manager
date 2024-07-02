package com.hand.hls.vat.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.vat.dto.AcrInvoiceBill;

import java.util.List;

public interface AcrInvoiceBillMapper extends Mapper<AcrInvoiceBill>{

    List<AcrInvoiceBill> queryAcrInvoiceBillMaintenance();

    List<AcrInvoiceBill> queryAcrInvoiceBill();

}
