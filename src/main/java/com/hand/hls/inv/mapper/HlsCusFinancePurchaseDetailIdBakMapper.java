package com.hand.hls.inv.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.inv.dto.HlsCusFinancePurchaseDetailIdBak;

import java.util.List;

public interface HlsCusFinancePurchaseDetailIdBakMapper extends Mapper<HlsCusFinancePurchaseDetailIdBak> {
    List<HlsCusFinancePurchaseDetailIdBak> queryAll(HlsCusFinancePurchaseDetailIdBak bak);

    void updateDetailId(HlsCusFinancePurchaseDetailIdBak bak);
}