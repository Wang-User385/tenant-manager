package com.hand.hls.fct.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.fct.dto.HlsCreditChanceLeaseItem;
import com.hand.hls.fct.service.IHlsCreditChanceLeaseItemService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCreditChanceLeaseItemServiceImpl extends BaseServiceImpl<HlsCreditChanceLeaseItem> implements IHlsCreditChanceLeaseItemService{

}