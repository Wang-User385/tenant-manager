package com.hand.hls.sys.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.sys.dto.SysUserAuthorityRule;
import com.hand.hls.sys.mapper.SysUserAuthorityRuleMapper;
import com.hand.hls.sys.service.SysUserAuthorityRuleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Created by 罗宇娟 on 2017/5/15.
 */
@Service
@Transactional
public class SysUserAuthorityRuleServiceImpl extends BaseServiceImpl<SysUserAuthorityRule> implements SysUserAuthorityRuleService {
    @Autowired
    private SysUserAuthorityRuleMapper mapper;


    @Override
    public List<SysUserAuthorityRule> selectList(IRequest iRequest, SysUserAuthorityRule sysUserAuthorityRule, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return mapper.selectList(sysUserAuthorityRule);
    }

    @Override
    public ResponseData saveSysUserAuthorityRules(List<SysUserAuthorityRule> sysUserAuthorityRules, IRequest iRequest) {
        for (int i = 0; i < sysUserAuthorityRules.size(); i++) {
            SysUserAuthorityRule rule = sysUserAuthorityRules.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(getRuleString(rule.getCompanyRule())).append(".")
                    .append(getRuleString(rule.getOrgUnitRule())).append(".")
                    .append(getRuleString(rule.getPositionRule())).append(".");
            if (Objects.equals("PRJ_PROJECT", rule.getDocumentCategory())) {
                sb.append(getRuleString("")).append(".");
                sb.append(getRuleString(rule.getBizTypeRule())).append(".");
                sb.append(getRuleString(rule.getDivisionRule())).append(".");
            } else if (Objects.equals("CON_CONTRACT", rule.getDocumentCategory())) {
                sb.append(getRuleString(rule.getDocTypeRule())).append(".")
                        .append(getRuleString(rule.getBizTypeRule())).append(".")
                        .append(getRuleString(rule.getDivisionRule())).append(".");
            } else if (Objects.equals("HLS_BP_MASTER", rule.getDocumentCategory())) {
                sb.append(getRuleString("")).append(".");
                sb.append(getRuleString("")).append(".");
                sb.append(getRuleString(rule.getPositionRule())).append(".");
            }
            sb.append(getRuleString(rule.getEmployeeRule()));
            rule.setAuthorityRuleString(sb.toString());
            rule.setEnabledFlag("Y");
            rule.setObjectVersionNumber(null);
        }
        return new ResponseData(self().batchUpdate(iRequest, sysUserAuthorityRules));
    }

    private String getRuleString(String rule) {
        if (StringUtils.isNotEmpty(rule)) {
            return "%\"" + rule + "\"%";
        } else {
            return "%%";
        }
    }

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }
}
