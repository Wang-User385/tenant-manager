package com.hand.hls.hls.mapper;

import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.sys.dto.SysTemplate;
import uncertain.composite.CompositeMap;

import java.util.List;
import java.util.Map;

public interface HlsCusHlsMarketingReportMapper extends HlsMarketingReportMapper<HlsCusHlsMarketingReport>{

    List<HlsCusHlsMarketingReport> queryMarketingReport(HlsCusHlsMarketingReport hlsCusHlsMarketingReport);
    List<HlsCusHlsMarketingReport> queryMarketingReportHome(HlsCusHlsMarketingReport hlsCusHlsMarketingReport);
    List<HlsCusHlsMarketingReport> queryMarketingReportDetail(HlsCusHlsMarketingReport hlsCusHlsMarketingReport);
    List<Map> applyLeasingProductTree();

    List<Map> applyLeasingProductTreeForPq();
    List<CompositeMap> queryMarketingReportId(CompositeMap var1, String var2);
    List<HlsCusHlsMarketingReport> queryDetail(HlsCusHlsMarketingReport hlsMarketingReportMapper);
    HlsCusHlsMarketingReport queryMarketingReportIdNew();
}