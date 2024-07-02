package com.hand.hls.fin.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusCtLonContractBankAccount;

import com.hand.hls.fin.mapper.HlsCusCtLonContractBankAccountMapper;
import com.hand.hls.fin.service.HlsCusCtLonContractBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCtLonContractBankAccountServiceImpl extends BaseServiceImpl<HlsCusCtLonContractBankAccount> implements HlsCusCtLonContractBankAccountService {

    @Autowired
    private HlsCusCtLonContractBankAccountMapper lonContractBankAccountMapper;

    @Override
    public List<HlsCusCtLonContractBankAccount> selectConBankAccount(IRequest iRequest, HlsCusCtLonContractBankAccount lonContractBankAccount, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        List<HlsCusCtLonContractBankAccount> list = lonContractBankAccountMapper.selectConBankAccount(lonContractBankAccount);
        return list;
    }

    @Override
    public int selectBankAccountTypeCount(HlsCusCtLonContractBankAccount lonContractBankAccount) {
        return lonContractBankAccountMapper.selectBankAccountTypeCount(lonContractBankAccount);
    }

    @Override
    public void updateSelective(IRequest iRequest,HlsCusCtLonContractBankAccount lonContractBankAccount) {
        lonContractBankAccountMapper.updateSelective(lonContractBankAccount);
    }
}