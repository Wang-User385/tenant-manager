package com.hand.hls.common.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.common.dto.HlsCusDocumentRecordList;
import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;

public interface HlsCusDocumentRecordListService extends IBaseService<HlsCusDocumentRecordList>, ProxySelf<HlsCusDocumentRecordListService>{


    HlsCusHapInterfaceOutbound wsInterfaceUnifiedResend(IRequest iRequest, HlsCusHapInterfaceOutbound dto);
}