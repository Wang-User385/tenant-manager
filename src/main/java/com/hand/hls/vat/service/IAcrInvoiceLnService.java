package com.hand.hls.vat.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.vat.dto.AcrInvoiceLn;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceLn;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceLn;
import com.hand.hls.vat.exception.AcrInvoiceException;

import java.util.List;

public interface IAcrInvoiceLnService extends IBaseService<HlsCusAcrInvoiceLn>, ProxySelf<IAcrInvoiceLnService> {
    List<AcrInvoiceLn> selectForCreate(List<Long> var1, String var2,String billingType) throws AcrInvoiceException;

    List<AcrInvoiceLn> queryAcrInvoiceLnDetailByHdId(Long var1, int var2, int var3);

    List<HlsCusAcrInvoiceLn> selectImportTempList(Long var1);
    List<Double> queryTotalAmount(HlsCusAcpInvoiceLn hlsCusAcpInvoiceLn);
    List<Double> queryContractAmount(HlsCusAcpInvoiceLn hlsCusAcpInvoiceLn);

}