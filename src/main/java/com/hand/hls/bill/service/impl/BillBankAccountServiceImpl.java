package com.hand.hls.bill.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.bill.dto.BillBankAccount;
import com.hand.hls.bill.service.BillBankAccountService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class BillBankAccountServiceImpl extends BaseServiceImpl<BillBankAccount> implements BillBankAccountService{

}