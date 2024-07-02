package com.hand.hls.cont.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.DocFileTempletRule;
import com.hand.hls.cont.dto.DocFileTempletType;
import com.hand.hls.ruleengine.dto.RuleEngineType;

public interface IDocFileTempletRuleService extends IBaseService<DocFileTempletRule>, ProxySelf<IDocFileTempletRuleService> {

    List<DocFileTempletRule> selectByFontCondition(IRequest request, DocFileTempletRule docFileTempletRule, int page, int pageSize);

    void updateRuleEngineTypleByRuleEngineId(DocFileTempletRule docFileTempletRule);

    List<DocFileTempletType> selectTempletTypeLov(DocFileTempletType var1);
}