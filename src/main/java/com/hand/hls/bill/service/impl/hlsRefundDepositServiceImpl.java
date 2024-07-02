package com.hand.hls.bill.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.bill.dto.hlsRefundDeposit;
import com.hand.hls.bill.service.IhlsRefundDepositService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class hlsRefundDepositServiceImpl extends BaseServiceImpl<hlsRefundDeposit> implements IhlsRefundDepositService{

}