package com.hand.hls.hls.mapper;

import com.hand.hls.hls.dto.HlsCusHlsCreditLineTrx;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReportBp;

import java.util.List;

public interface HlsCusHlsMarketingReportBpMapper extends HlsMarketingReportBpMapper<HlsCusHlsMarketingReportBp>{

    List<HlsCusHlsMarketingReportBp> queryMarketingReportBp(HlsCusHlsMarketingReportBp hlsCusHlsMarketingReportBp);
}