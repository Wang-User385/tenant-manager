package com.hand.hls.fct.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hls.atm.dto.FndAttachment;

import java.util.List;
import java.util.Map;

public interface HlsCreditChanceDocxService {

    List<FndAttachment> process(IRequest requestContext, Map<String, Object> params, JSONObject jsonObject) throws Exception;


}
