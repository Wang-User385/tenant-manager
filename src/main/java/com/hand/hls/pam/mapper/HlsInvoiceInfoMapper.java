package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.HlsInvoiceInfo;

import java.util.List;

public interface HlsInvoiceInfoMapper extends Mapper<HlsInvoiceInfo>{
    List<HlsInvoiceInfo> searchInvoiceLnListQuery(HlsInvoiceInfo hlsInvoiceInfo);
}