package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.MarketingReportChannel;

import java.util.List;
import java.util.Map;

public interface MarketingReportChannelMapper extends Mapper<MarketingReportChannel>{
    List<MarketingReportChannel> queryReportChannel(MarketingReportChannel marketingReportChannel);
    List<Map> industryTree();
}