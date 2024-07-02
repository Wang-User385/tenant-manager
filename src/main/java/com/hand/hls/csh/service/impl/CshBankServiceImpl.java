//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.HlsCusCshBank;
import com.hand.hls.csh.mapper.HlsCusCshBankMapper;
import com.hand.hls.csh.service.CshBankService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class CshBankServiceImpl extends BaseServiceImpl<HlsCusCshBank> implements CshBankService {
    @Autowired
    HlsCusCshBankMapper mapper;

    public CshBankServiceImpl() {
    }

    public List<HlsCusCshBank> queryCshBank(IRequest requestCtx, HlsCusCshBank hlsCusCshBank, int pagenum, int pagesize) {
        return this.mapper.queryCshBank2(hlsCusCshBank);
    }
}
