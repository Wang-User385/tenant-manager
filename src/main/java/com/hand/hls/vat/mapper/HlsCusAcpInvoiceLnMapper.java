//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.vat.mapper;

import com.hand.hls.vat.dto.HlsCusAcpInvoiceLn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusAcpInvoiceLnMapper extends AcpInvoiceLnMapper<HlsCusAcpInvoiceLn> {
    List<HlsCusAcpInvoiceLn> searchInvoiceLnByContractId(HlsCusAcpInvoiceLn hlsCusAcpInvoiceLn);
    Double queryTotalAmount(@Param("contractId") Long contractId,@Param("invoiceLnId") Long invoiceLnId);
    Double queryContractAmount(@Param("contractId") Long contractId);
    Double queryTotalAmountNew(@Param("contractId") Long contractId);

}
