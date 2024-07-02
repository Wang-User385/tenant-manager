//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.cont.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.mapper.HlsCusBpMasterBankAccountMapper;
import com.hand.hls.cont.dto.HlsCusHlsBpMasterBankAccount;
import com.hand.hls.cont.service.HlsBpMasterBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Primary
public class HlsBpMasterBankAccountServiceImpl extends BaseServiceImpl<HlsCusBpMasterBankAccount> implements HlsBpMasterBankAccountService {
    @Autowired
    private HlsCusBpMasterBankAccountMapper mapper;

    public HlsBpMasterBankAccountServiceImpl() {
    }

    @Override
    public List<HlsCusBpMasterBankAccount> selectAll(IRequest requestContext, HlsCusBpMasterBankAccount bpMasterBankAccount, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return this.mapper.queryAll(bpMasterBankAccount);
    }

    @Override
    public List<HlsCusBpMasterBankAccount> debtAccountQuery(IRequest requestContext, HlsCusBpMasterBankAccount bpMasterBankAccount, int page, int pagesize) {
        return this.mapper.debtAccountQuery(bpMasterBankAccount);
    }

    @Override
    public void modifyBpMasterBankAccountDeptBankId(HlsCusBpMasterBankAccount bpMasterBankAccount) {
        this.mapper.modifyBpMasterBankAccountDeptBankId(bpMasterBankAccount);
    }

    @Override
    public List<HlsCusHlsBpMasterBankAccount> queryHlsBpMasterBankAccountLov(IRequest requestCtx, HlsCusHlsBpMasterBankAccount hlsCusHlsBpMasterBankAccount, int pagenum, int pagesize) {

        PageHelper.startPage(pagenum, pagesize);
        return mapper.queryHlsBpMasterBankAccountLov(hlsCusHlsBpMasterBankAccount);
    }

}
