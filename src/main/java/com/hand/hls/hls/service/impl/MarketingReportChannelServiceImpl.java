package com.hand.hls.hls.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.MarketingReportChannel;
import com.hand.hls.hls.service.IMarketingReportChannelService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class MarketingReportChannelServiceImpl extends BaseServiceImpl<MarketingReportChannel> implements IMarketingReportChannelService{

}