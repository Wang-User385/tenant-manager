package com.hand.hls.ruleengine.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hls.ruleengine.dto.HlsRuleEngineRoute;

import java.util.List;
import java.util.Map;

/**
 * Created by gaoyang on 2017/5/12.
 */
public interface IHLSRuleEngineInitService {
    String[] ruleEngineInit(IRequest iRequest, JSONObject jsonObject, List<HlsRuleEngineRoute> routeList);
    String[] ruleEngineInit(IRequest iRequest, JSONObject jsonObject);
    /**
     2017/10/9新增凭证规则编辑
     */
    Map<String,Object> gldRuleEngineInit(IRequest iRequest, JSONObject jsonObject);
}
