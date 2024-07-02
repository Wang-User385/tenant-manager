package com.hand.hls.csh.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.csh.dto.CshPaymentReqSlip;
import com.hand.hls.csh.service.ICshPaymentReqSlipService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class CshPaymentReqSlipServiceImpl extends BaseServiceImpl<CshPaymentReqSlip> implements ICshPaymentReqSlipService{

}