package com.hand.hls.fin.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusCtLonContractConsignmentSalesInfo;

import java.util.List;

public interface HlsCusCtLonContractConsignmentSalesInfoService extends IBaseService<HlsCusCtLonContractConsignmentSalesInfo>, ProxySelf<HlsCusCtLonContractConsignmentSalesInfoService> {
    List<HlsCusCtLonContractConsignmentSalesInfo> selectConsignmentSalesList(HlsCusCtLonContractConsignmentSalesInfo hlsCusCtLonContractConsignmentSalesInfo);
}