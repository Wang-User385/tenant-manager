package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.HlsCusCshBankAccount;
import com.hand.hls.csh.mapper.HlsCusCshBankAccountMapper;
import com.hand.hls.csh.service.HlsCusCshBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:重写收付管理查询方法
 * @Author: wty
 * @Date: Created in 13:11 2018/5/14
 */
@Service
@Transactional
public class HlsCusCshBankAccountServiceImpl extends BaseServiceImpl<HlsCusCshBankAccount> implements HlsCusCshBankAccountService {

    @Autowired
    HlsCusCshBankAccountMapper mapper;

    @Override
    public List<HlsCusCshBankAccount> selectCshBankAccountLov(HlsCusCshBankAccount hlsCusCshBankAccount, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return mapper.selectCshBankAccountLov(hlsCusCshBankAccount);
    }
}
