package com.hand.hls.hls.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.hls.dto.HlsCusOffBalanceAccount;
import com.hand.hls.hls.service.HlsCusOffBalanceAccountService;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.OffBalanceAccount;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusOffBalanceAccountServiceImpl extends BaseServiceImpl<HlsCusOffBalanceAccount> implements HlsCusOffBalanceAccountService {

}
