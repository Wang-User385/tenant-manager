package com.hand.hls.inv.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.inv.dto.HlsCusInvFinancialProducts;

import java.util.List;

public interface HlsCusInvFinancialProductsMapper extends Mapper<HlsCusInvFinancialProducts> {

    //查询产品列表
    List<HlsCusInvFinancialProducts> queryProductsList(HlsCusInvFinancialProducts hlsCusInvFinancialProducts);

}