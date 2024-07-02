package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsBankAccount;
import com.hand.hls.abs.mapper.HlsCusAbsBankAccountMapper;
import com.hand.hls.abs.service.HlsCusAbsBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsBankAccountServiceImpl extends BaseServiceImpl<HlsCusAbsBankAccount> implements HlsCusAbsBankAccountService {


    @Autowired
    private HlsCusAbsBankAccountMapper bankAccountMapper;


    @Override
    public List<HlsCusAbsBankAccount> selectAbsBankAccountData(IRequest iRequest, HlsCusAbsBankAccount bankAccount, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return bankAccountMapper.selectAbsBankAccountData(bankAccount);
    }


    @Override
    public List<HlsCusAbsBankAccount> selectDistinctAbsCompany(IRequest iRequest, HlsCusAbsBankAccount bankAccount) {

        return bankAccountMapper.selectDistinctAbsCompany(bankAccount);
    }

    @Override
    public int selectOtherDataQuoteCount(IRequest iRequest, HlsCusAbsBankAccount bankAccount) {

        return bankAccountMapper.selectOtherDataQuoteCount(bankAccount);
    }
}