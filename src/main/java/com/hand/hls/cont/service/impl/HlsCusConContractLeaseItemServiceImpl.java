package com.hand.hls.cont.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractLeaseItem;
import com.hand.hls.cont.service.HlsCusConContractLeaseItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractLeaseItemServiceImpl extends BaseServiceImpl<HlsCusConContractLeaseItem> implements HlsCusConContractLeaseItemService {

}