package com.hand.hls.cont.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractBeforeRentH;
import com.hand.hls.cont.service.HlsCusConContractBeforeRentHService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractBeforeRentHServiceImpl extends BaseServiceImpl<HlsCusConContractBeforeRentH> implements HlsCusConContractBeforeRentHService {

}