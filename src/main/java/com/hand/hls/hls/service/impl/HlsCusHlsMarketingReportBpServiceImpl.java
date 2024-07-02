package com.hand.hls.hls.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReportBp;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportBpService;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.HlsMarketingReportBp;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsMarketingReportBpServiceImpl extends BaseServiceImpl<HlsCusHlsMarketingReportBp> implements HlsCusHlsMarketingReportBpService {

}