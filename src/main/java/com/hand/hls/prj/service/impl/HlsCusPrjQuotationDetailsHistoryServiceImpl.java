package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetailsHistory;
import com.hand.hls.prj.service.IHlsCusPrjQuotationDetailsHistoryService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjQuotationDetailsHistoryServiceImpl extends BaseServiceImpl<HlsCusPrjQuotationDetailsHistory> implements IHlsCusPrjQuotationDetailsHistoryService {

}