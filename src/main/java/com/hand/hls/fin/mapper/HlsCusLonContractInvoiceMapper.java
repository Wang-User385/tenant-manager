package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusLonContractInvoice;

import java.util.List;

public interface HlsCusLonContractInvoiceMapper extends Mapper<HlsCusLonContractInvoice> {

    List<HlsCusLonContractInvoice> selectInvoiceInfo(HlsCusLonContractInvoice hlsCusLonContractInvoice);
}
