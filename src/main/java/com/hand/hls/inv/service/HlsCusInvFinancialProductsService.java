package com.hand.hls.inv.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.inv.dto.HlsCusInvFinancialProducts;

import java.util.List;

public interface HlsCusInvFinancialProductsService extends IBaseService<HlsCusInvFinancialProducts>, ProxySelf<HlsCusInvFinancialProductsService> {


    //查询产品列表
    List<HlsCusInvFinancialProducts> queryProductsList(IRequest iRequest, HlsCusInvFinancialProducts hlsCusInvFinancialProducts, int page, int pageSize);
}