package com.hand.hls.hls.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.hls.dto.HlsCusCapMarketingChannel;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.service.IMarketingChannelService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class MarketingChannelServiceImpl extends BaseServiceImpl<HlsCusCapMarketingChannel> implements IMarketingChannelService{

}