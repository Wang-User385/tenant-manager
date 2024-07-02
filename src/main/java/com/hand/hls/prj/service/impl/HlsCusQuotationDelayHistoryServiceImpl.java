package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.prj.dto.HlsCusQuotationDelayHistory;
import com.hand.hls.prj.service.IHlsCusQuotationDelayHistoryService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusQuotationDelayHistoryServiceImpl extends BaseServiceImpl<HlsCusQuotationDelayHistory> implements IHlsCusQuotationDelayHistoryService{

}