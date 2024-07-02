package com.hand.hap.mail.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.mail.dto.SysMessageEmailRule;
import com.hand.hap.system.service.IBaseService;

import java.util.List;
import java.util.Map;

public interface SysMessageEmailRuleService extends IBaseService<SysMessageEmailRule>, ProxySelf<SysMessageEmailRuleService>{

    /**
     * 按照指定的规则调用邮件模版发送邮件
     * @param ruleCode 邮件规则定义
     * @param params 邮件模版中的参数键值对
     * @param attachmentIds 附件id集合
     *                      新事务
     */
    void sendEmail(IRequest iRequest, String ruleCode, Map<String, Object> params, List<Long> attachmentIds) throws Exception;
}
