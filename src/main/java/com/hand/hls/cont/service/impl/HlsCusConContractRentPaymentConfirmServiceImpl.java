package com.hand.hls.cont.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.cont.dto.HlsCusConContractRentPaymentConfirm;
import com.hand.hls.cont.service.IHlsCusConContractRentPaymentConfirmService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractRentPaymentConfirmServiceImpl extends BaseServiceImpl<HlsCusConContractRentPaymentConfirm> implements IHlsCusConContractRentPaymentConfirmService{

}