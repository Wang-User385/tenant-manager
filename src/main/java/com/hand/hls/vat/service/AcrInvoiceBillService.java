package com.hand.hls.vat.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.service.FndInterfaceLinesService;
import com.hand.hls.vat.dto.AcrInvoiceBill;

import java.util.List;

public interface AcrInvoiceBillService extends IBaseService<AcrInvoiceBill>, ProxySelf<AcrInvoiceBillService> {

    List<AcrInvoiceBill> queryAcrInvoiceBillMaintenance();

    List<AcrInvoiceBill> queryAcrInvoiceBill();

//    int createAcrInvoiceBill(AcrInvoiceBill acrInvoiceBill);
//
//    int updateAcrInvoiceBill(AcrInvoiceBill acrInvoiceBill);

}
