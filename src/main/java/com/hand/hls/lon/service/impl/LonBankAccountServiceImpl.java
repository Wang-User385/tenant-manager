package com.hand.hls.lon.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.lon.dto.LonBankAccount;
import com.hand.hls.lon.service.ILonBankAccountService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class LonBankAccountServiceImpl extends BaseServiceImpl<LonBankAccount> implements ILonBankAccountService{

}