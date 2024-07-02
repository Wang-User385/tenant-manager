package com.hand.hls.inv.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.inv.dto.HlsCusFinancePurchaseBak;

import java.util.List;

public interface HlsCusFinancePurchaseBakMapper extends Mapper<HlsCusFinancePurchaseBak> {
    List<HlsCusFinancePurchaseBak> queryAll(HlsCusFinancePurchaseBak hlsCusFinancePurchaseBak);

}