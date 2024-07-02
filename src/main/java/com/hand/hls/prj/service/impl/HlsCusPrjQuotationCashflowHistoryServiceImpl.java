package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflowHistory;
import com.hand.hls.prj.service.IHlsCusPrjQuotationCashflowHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjQuotationCashflowHistoryServiceImpl extends BaseServiceImpl<HlsCusPrjQuotationCashflowHistory> implements IHlsCusPrjQuotationCashflowHistoryService {

}