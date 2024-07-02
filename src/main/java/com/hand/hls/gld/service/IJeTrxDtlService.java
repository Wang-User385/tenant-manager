package com.hand.hls.gld.service;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.gld.dto.JeTrxDtl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IJeTrxDtlService extends IBaseService<JeTrxDtl>, ProxySelf<IJeTrxDtlService> {

    @Deprecated
    void jeTrxMade(IRequest request, HashMap map);

    ResponseData createJeLine(IRequest iRequest, List<Long> ids, Map param);

    ResponseData createJeLine(IRequest iRequest, List<Long> ids);

    List<JeTrxDtl> queryJeTrxDtlStatus();

   /*待生成清单查询*/
    @Deprecated
    List<JeTrxDtl> waitGenerateList(JeTrxDtl dto);

    /*待生成清单查询*/
    List<JeTrxDtl> waitGenerateList(JeTrxDtl dto, int page, int pageSize);

    List<JeTrxDtl> gld320aJeTrxDtlQuery(JeTrxDtl jeTrxDtl, IRequest iRequest, int page, int pagesize);

    void updateGldJeLineData(IRequest request, List<String> jeTemplateIds, List<String> gldRouteResult, Long sourceId, JSONObject json) throws ParseException, JSONException, NullPointerException, IllegalArgumentException;

    void reverseLine(IRequest iRequest, JeTrxDtl jeTrxDtl, JSONObject jsonObject);
}