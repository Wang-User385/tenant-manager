package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.HlsCheckItems;

import java.util.List;

public interface HlsCheckItemsMapper extends Mapper<HlsCheckItems>{

    List<HlsCheckItems> queryDistinctType();
}