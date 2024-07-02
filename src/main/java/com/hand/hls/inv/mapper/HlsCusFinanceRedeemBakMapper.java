package com.hand.hls.inv.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.inv.dto.HlsCusFinanceRedeemBak;

import java.util.List;

public interface HlsCusFinanceRedeemBakMapper extends Mapper<HlsCusFinanceRedeemBak> {
    List<HlsCusFinanceRedeemBak> queryAll(HlsCusFinanceRedeemBak hlsCusFinanceRedeemBak);

}