package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCreditChanceLeaseItem;

import java.util.List;

public interface HlsCreditChanceLeaseItemMapper extends Mapper<HlsCreditChanceLeaseItem>{
    List<HlsCreditChanceLeaseItem> queryCreditChanceLeaseItem(HlsCreditChanceLeaseItem hlsCreditChanceLeaseItem);
}