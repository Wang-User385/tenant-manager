package com.hand.hls.vat.mapper;

import com.hand.hls.vat.dto.HlsCusAcrInvoiceLn;

import java.util.List;

public interface HlsCusAcrInvoiceLnMapper extends AcrInvoiceLnMapper<HlsCusAcrInvoiceLn> {

    List<HlsCusAcrInvoiceLn> selectImportTempList(Long var1);

}
