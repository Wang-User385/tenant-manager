package com.hand.hap.mail.service.impl;

import com.hand.hap.mail.dto.SysMessageEmailRuleLn;
import com.hand.hap.mail.mapper.SysMessageEmailRuleLnMapper;
import com.hand.hap.mail.service.SysMessageEmailRuleLnService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class SysMessageEmailRuleLnServiceImpl extends BaseServiceImpl<SysMessageEmailRuleLn> implements SysMessageEmailRuleLnService{
    @Autowired
    private SysMessageEmailRuleLnMapper sysMessageEmailRuleLnMapper;

    @Override
    public List<SysMessageEmailRuleLn> queryData(Long ruleId) {
        Assert.notNull(ruleId, "规则主键不能为空");
        return sysMessageEmailRuleLnMapper.queryData(ruleId);
    }
}
