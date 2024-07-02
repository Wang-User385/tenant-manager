package com.hand.hls.gld.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.gld.mapper.AccountHierarchyDtlMapper;
import com.hand.hls.gld.mapper.AccountHierarchyMapper;
import com.hand.hls.gld.mapper.HlsCusAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.gld.dto.HlsCusAccount;
import com.hand.hls.gld.service.IHlsCusAccountService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAccountServiceImpl extends BaseServiceImpl<HlsCusAccount> implements IHlsCusAccountService{

    @Autowired
    private HlsCusAccountMapper accountMapper;
    @Autowired
    private AccountHierarchyMapper accountHierarchyMapper;
    @Autowired
    private AccountHierarchyDtlMapper accountHierarchyDtlMapper;

    public List<HlsCusAccount> queryAccountCode() {
        return this.accountMapper.queryAccountCode();
    }

    public List<HlsCusAccount> queryModify(IRequest requestCtx, HlsCusAccount metadataRelation, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return this.accountMapper.queryModify(metadataRelation);
    }

    public int batchDelete(List<HlsCusAccount> list) {
        list.forEach((item) -> {
            Long accountId = item.getAccountId();
            this.accountHierarchyDtlMapper.deleteByParentAccountId(accountId);
            this.accountHierarchyMapper.deleteByAccountId(accountId);
            item.setObjectVersionNumber((Long)null);
            ((IHlsCusAccountService)this.self()).deleteByPrimaryKey(item);
        });
        return list.size();
    }
}