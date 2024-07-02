package com.hand.hls.hn.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.hn.dto.PrjCheckPutPay;
import com.hand.hls.hn.service.IPrjCheckPutPayService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class PrjCheckPutPayServiceImpl extends BaseServiceImpl<PrjCheckPutPay> implements IPrjCheckPutPayService{

}