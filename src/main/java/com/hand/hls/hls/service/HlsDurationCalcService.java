package com.hand.hls.hls.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;

import java.util.List;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/6/11
 * @description:
 */
public interface HlsDurationCalcService extends IBaseService<HlsDurationHd>, ProxySelf<HlsDurationCalcService> {

    List<HlsDurationLn> calculate(IRequest iRequest, Long lnId, JSONObject param) throws Exception;

    HlsDurationLn createQuotation(IRequest iRequest,Long lnId);
}
