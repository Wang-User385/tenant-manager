package com.hand.hls.vat.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpFinancialHeader;
import com.hand.hls.vat.dto.HlsInvoiceInterfaceInfo;

public interface HlsInvoiceInterfaceInfoMapper extends Mapper<HlsInvoiceInterfaceInfo>{
    void deleteByDocument(HlsInvoiceInterfaceInfo info);
}