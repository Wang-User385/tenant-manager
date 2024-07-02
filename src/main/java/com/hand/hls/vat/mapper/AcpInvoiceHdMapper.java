package com.hand.hls.vat.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceHd;

import java.util.List;
import java.util.Map;

public interface AcpInvoiceHdMapper<T extends HlsCusAcpInvoiceHd> extends Mapper<HlsCusAcpInvoiceHd> {
    List<HlsCusAcpInvoiceHd> acpInvoiceQuery(Map<String, Object> var1);

    List<HlsCusAcpInvoiceHd> acpInvoiceScaleQuery();

    List<HlsCusAcpInvoiceHd> acpInvoicedTotleQuery();

    List<HlsCusAcpInvoiceHd> acpInvoicedGroupQuery(Long var1);
}
