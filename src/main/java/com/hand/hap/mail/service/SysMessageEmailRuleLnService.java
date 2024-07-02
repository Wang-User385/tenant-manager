package com.hand.hap.mail.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.mail.dto.SysMessageEmailRuleLn;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

public interface SysMessageEmailRuleLnService extends IBaseService<SysMessageEmailRuleLn>, ProxySelf<SysMessageEmailRuleLnService>{
    /**
     * 查询收件人数据
     */
    List<SysMessageEmailRuleLn> queryData(Long ruleId);
}
