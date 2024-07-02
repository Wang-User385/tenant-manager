package com.hand.hap.mail.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.mail.dto.MessageReceiver;
import com.hand.hap.mail.dto.SysMessageEmailRule;
import com.hand.hap.mail.dto.SysMessageEmailRuleLn;
import com.hand.hap.mail.mapper.SysMessageEmailRuleMapper;
import com.hand.hap.mail.service.IMessageService;
import com.hand.hap.mail.service.SysMessageEmailRuleLnService;
import com.hand.hap.mail.service.SysMessageEmailRuleService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class SysMessageEmailRuleServiceImpl extends BaseServiceImpl<SysMessageEmailRule> implements SysMessageEmailRuleService {
    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(SysMessageEmailRuleServiceImpl.class);

    @Autowired
    private SysMessageEmailRuleMapper sysMessageEmailRuleMapper;
    @Autowired
    private SysMessageEmailRuleLnService sysMessageEmailRuleLnService;
    @Autowired
    private IMessageService messageService;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void sendEmail(IRequest iRequest, String ruleCode, Map<String, Object> params, List<Long> attachmentIds)
            throws Exception {
        Assert.hasText(ruleCode, "规则代码不能为空");
        SysMessageEmailRule rule = sysMessageEmailRuleMapper.queryForSendEmailByRuleCode(ruleCode);
        Assert.notNull(rule, "邮件调用规则未定义");

        List<SysMessageEmailRuleLn> ruleLnList = sysMessageEmailRuleLnService.queryData(rule.getRuleId());

        List<MessageReceiver> receiverList = new ArrayList<>();
        for (SysMessageEmailRuleLn ruleLn : ruleLnList) {
            MessageReceiver mr = new MessageReceiver();
            mr.setMessageAddress(ruleLn.getEmail());
            mr.setMessageType(ruleLn.getType().toUpperCase());
            receiverList.add(mr);
        }

        messageService.sendMessage(iRequest, rule.getTemplateCode(), params, receiverList, attachmentIds);
    }
}
