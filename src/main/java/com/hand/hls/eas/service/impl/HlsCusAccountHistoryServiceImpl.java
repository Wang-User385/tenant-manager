package com.hand.hls.eas.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.eas.dto.HlsCusAccountHistory;
import com.hand.hls.eas.service.IHlsCusAccountHistoryService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAccountHistoryServiceImpl extends BaseServiceImpl<HlsCusAccountHistory> implements IHlsCusAccountHistoryService{

}