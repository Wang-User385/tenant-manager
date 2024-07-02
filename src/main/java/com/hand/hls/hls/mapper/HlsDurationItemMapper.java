package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsDurationItem;
import com.hand.hls.hls.dto.HlsDurationLn;

import java.util.List;

public interface HlsDurationItemMapper extends Mapper<HlsDurationItem>{


    List<HlsDurationItem> hlsDurationItemPledgorMortgagorQuery(HlsDurationItem ln);

    List<HlsDurationItem> hlsDurationItemLeaseItemQuery(HlsDurationItem ln);

    List<HlsDurationItem> hlsDurationItemPledgorMortgagorQueryNew(HlsDurationItem ln);

    List<HlsDurationItem> hlsDurationItemLeaseItemQueryNew(HlsDurationItem ln);
}