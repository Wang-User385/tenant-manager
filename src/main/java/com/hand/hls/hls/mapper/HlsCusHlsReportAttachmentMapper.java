package com.hand.hls.hls.mapper;

import com.hand.hls.hls.dto.HlsCusHlsReportAttachment;

import java.util.List;

public interface HlsCusHlsReportAttachmentMapper extends HlsReportAttachmentMapper<HlsCusHlsReportAttachment>{

    List<HlsCusHlsReportAttachment> queryReportAttachment(HlsCusHlsReportAttachment hlsCusHlsReportAttachment);
}