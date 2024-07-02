package com.hand.hls.vat.mapper;


import com.hand.hls.vat.dto.HlsCusAcpInvoiceHd;

import java.util.List;
import java.util.Map;

public interface HlsCusAcpInvoiceHdMapper extends AcpInvoiceHdMapper<HlsCusAcpInvoiceHd> {
    List<Map> queryVatAcpInvoiceDetail(Map param);


}
