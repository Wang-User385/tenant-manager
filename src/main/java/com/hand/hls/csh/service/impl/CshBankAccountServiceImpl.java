//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.HlsCusCshBankAccount;
import com.hand.hls.csh.mapper.HlsCusCshBankAccountMapper;
import com.hand.hls.csh.service.CshBankAccountService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class CshBankAccountServiceImpl extends BaseServiceImpl<HlsCusCshBankAccount> implements CshBankAccountService {
    @Autowired
    HlsCusCshBankAccountMapper mapper;

    public CshBankAccountServiceImpl() {
    }

    public List<HlsCusCshBankAccount> queryCshBankAccount(IRequest requestCtx, HlsCusCshBankAccount hlsCusCshBankAccount, int pagenum, int pagesize) {
        return this.mapper.queryCshBankAccount2(hlsCusCshBankAccount);
    }
}
