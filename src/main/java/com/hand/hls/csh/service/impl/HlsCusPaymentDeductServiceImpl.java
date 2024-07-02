package com.hand.hls.csh.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.HlsCusPaymentDeduct;
import com.hand.hls.csh.service.HlsCusPaymentDeductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPaymentDeductServiceImpl extends BaseServiceImpl<HlsCusPaymentDeduct> implements HlsCusPaymentDeductService {

}
