package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusCtLonContractBankAccount;

import java.util.List;

public interface HlsCusCtLonContractBankAccountMapper extends Mapper<HlsCusCtLonContractBankAccount> {


    /**
     * 查询
     * @param lonContractBankAccount
     * @return
     */
    List<HlsCusCtLonContractBankAccount> selectConBankAccount(HlsCusCtLonContractBankAccount lonContractBankAccount);

    /**
     * 查询账户类型唯一
     * @param lonContractBankAccount
     * @return
     */
    int selectBankAccountTypeCount(HlsCusCtLonContractBankAccount lonContractBankAccount);
    void deleteBankAccountByWithdrawId(HlsCusCtLonContractBankAccount hlsCusCtLonContractBankAccount);
    //插入新的账户信息
    void updateSelective(HlsCusCtLonContractBankAccount hlsCusCtLonContractBankAccount);


}