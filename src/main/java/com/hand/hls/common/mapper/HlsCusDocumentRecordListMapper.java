package com.hand.hls.common.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.common.dto.HlsCusDocumentRecordList;

import java.util.List;

public interface HlsCusDocumentRecordListMapper extends Mapper<HlsCusDocumentRecordList>{

  List<HlsCusDocumentRecordList> selectDocumentByOutboundId(HlsCusDocumentRecordList dto);

}