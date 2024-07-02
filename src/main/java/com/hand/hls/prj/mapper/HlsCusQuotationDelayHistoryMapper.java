package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusQuotationDelayHistory;

import java.util.List;

public interface HlsCusQuotationDelayHistoryMapper extends Mapper<HlsCusQuotationDelayHistory>{

    List<HlsCusQuotationDelayHistory> queryHistoryDetail(HlsCusQuotationDelayHistory hlsCusQuotationDelayHistory);
}