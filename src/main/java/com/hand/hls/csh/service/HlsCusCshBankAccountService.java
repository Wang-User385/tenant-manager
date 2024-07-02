package com.hand.hls.csh.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshBankAccount;

import java.util.List;

/**
 * @Description:
 * @Author: wty
 * @Date: Created in 13:09 2018/5/14
 */
public interface HlsCusCshBankAccountService extends IBaseService<HlsCusCshBankAccount>, ProxySelf<HlsCusCshBankAccountService> {

    List<HlsCusCshBankAccount> selectCshBankAccountLov(HlsCusCshBankAccount hlsCusCshBankAccount, int page, int pageSize);

}
