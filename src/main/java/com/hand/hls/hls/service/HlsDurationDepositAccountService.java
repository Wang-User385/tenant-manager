package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.HlsDurationDeposit;
import com.hand.hls.hls.dto.HlsDurationDepositAccount;
import com.hand.hls.utils.ResMessageException;

import java.util.List;

public interface HlsDurationDepositAccountService extends IBaseService<HlsDurationDepositAccount>, ProxySelf<HlsDurationDepositAccountService>{
    List<HlsDurationDepositAccount> hlsDurationDepositAccountDetailQuery (HlsDurationDepositAccount hlsDurationDepositAccount, int page, int pagesize);
    List<HlsDurationDepositAccount> hlsDurationDepositAccountSave(IRequest iRequest, List<HlsDurationDepositAccount> list) throws ResMessageException;
}