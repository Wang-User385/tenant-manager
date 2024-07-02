package com.hand.hls.plm.pli.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.plm.pli.dto.PliLeaseItem;

import java.util.List;

public interface PliLeaseItemMapper extends Mapper<PliLeaseItem> {
    List<PliLeaseItem> queryPliLeaseItemInfo(PliLeaseItem dto);
}