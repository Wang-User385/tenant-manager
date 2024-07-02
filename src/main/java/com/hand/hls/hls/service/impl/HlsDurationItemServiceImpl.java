package com.hand.hls.hls.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.HlsDurationItem;
import com.hand.hls.hls.service.HlsDurationItemService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsDurationItemServiceImpl extends BaseServiceImpl<HlsDurationItem> implements HlsDurationItemService{

}