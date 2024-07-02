package com.hand.hls.eas.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.eas.dto.HlsCusBankAccountHistory;
import com.hand.hls.eas.service.IHlsCusBankAccountHistoryService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBankAccountHistoryServiceImpl extends BaseServiceImpl<HlsCusBankAccountHistory> implements IHlsCusBankAccountHistoryService{

}