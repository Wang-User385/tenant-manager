package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusCtLonContractBankAccount;

import java.util.List;

public interface HlsCusCtLonContractBankAccountService extends IBaseService<HlsCusCtLonContractBankAccount>, ProxySelf<HlsCusCtLonContractBankAccountService> {


    /**
     * 查询
     * @param iRequest
     * @param lonContractBankAccount
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusCtLonContractBankAccount> selectConBankAccount(IRequest iRequest, HlsCusCtLonContractBankAccount lonContractBankAccount, int page, int pageSize);


    /**
     * 查询账户类型唯一
     * @param lonContractBankAccount
     * @return
     */
    int selectBankAccountTypeCount(HlsCusCtLonContractBankAccount lonContractBankAccount);

    void updateSelective(IRequest iRequest,HlsCusCtLonContractBankAccount lonContractBankAccount);


}