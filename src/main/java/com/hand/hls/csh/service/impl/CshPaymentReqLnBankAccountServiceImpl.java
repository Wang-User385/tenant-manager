package com.hand.hls.csh.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.CshPaymentReqLnBankAccount;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.service.CshPaymentReqLnBankAccountService;
import com.hand.hls.csh.service.CshPaymentReqLnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CshPaymentReqLnBankAccountServiceImpl extends BaseServiceImpl<CshPaymentReqLnBankAccount> implements CshPaymentReqLnBankAccountService {

}
