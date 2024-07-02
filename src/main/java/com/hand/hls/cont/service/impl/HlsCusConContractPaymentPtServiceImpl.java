package com.hand.hls.cont.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractPaymentPt;
import com.hand.hls.cont.service.HlsCusConContractPaymentPtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractPaymentPtServiceImpl extends BaseServiceImpl<HlsCusConContractPaymentPt> implements HlsCusConContractPaymentPtService {

}