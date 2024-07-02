package com.hand.hls.vat.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import com.hand.hls.vat.exception.AcrInvoiceException;
import java.util.List;

public interface IAcrInvoiceHdService extends IBaseService<HlsCusAcrInvoiceHd>, ProxySelf<IAcrInvoiceHdService> {
    List<HlsCusAcrInvoiceHd> selectForCreate(String var1, List<Long> var2, String var3) throws AcrInvoiceException;

    void create(IRequest var1, List<HlsCusAcrInvoiceHd> var2) throws AcrInvoiceException;

    void confirm(IRequest var1, List<HlsCusAcrInvoiceHd> var2) throws AcrInvoiceException;

    List<HlsCusAcrInvoiceHd> delete(IRequest var1, List<HlsCusAcrInvoiceHd> var2) throws AcrInvoiceException;

    void reverse(IRequest var1, List<HlsCusAcrInvoiceHd> var2, String var3) throws AcrInvoiceException;

    List<HlsCusAcrInvoiceHd> queryAcrInvoiceHdDetail(HlsCusAcrInvoiceHd var1, int var2, int var3);

    List<HlsCusAcrInvoiceHd> searchInvoicHdHomeQuery(HlsCusAcrInvoiceHd var1, int var2, int var3);

    HlsCusAcrInvoiceHd queryAcrInvoiceHdDetailById(Long var1);

    void updateConContractCashflow(IRequest requestCtx, HlsCusConContractCashflow conContractCashflow);
}
