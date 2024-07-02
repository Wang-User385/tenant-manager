package com.hand.hls.common.controllers;

import com.hand.hls.common.mapper.HlsCusItfcBankFlowMapper;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.common.dto.HlsCusItfcBankFlow;
import com.hand.hls.common.service.HlsCusItfcBankFlowService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class HlsCusItfcBankFlowController extends BaseController {

    @Autowired
    private HlsCusItfcBankFlowService service;
    @Autowired
    private HlsCusItfcBankFlowMapper mapper;


    @RequestMapping(value = "/hls/itfc/bank/flow/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusItfcBankFlow dto = param.toJavaObject(HlsCusItfcBankFlow.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/itfc/bank/flow/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusItfcBankFlow> list = param.toJavaList(HlsCusItfcBankFlow.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/itfc/bank/flow/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusItfcBankFlow> dto = parameter.toJavaList(HlsCusItfcBankFlow.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/hls/itfc/bank/flow/queryInfo")
    @ResponseBody
    public ResponseData queryInfo(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        HlsCusItfcBankFlow dto = (HlsCusItfcBankFlow) parameter.toJavaObject(HlsCusItfcBankFlow.class);
        List<HlsCusItfcBankFlow> list = mapper.queryBankFlowInfo(dto);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/itfc/bank/flow/dealRivalAccount")
    @ResponseBody
    public ResponseData dealRivalAccount(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusItfcBankFlow> list = parameter.toJavaList(HlsCusItfcBankFlow.class);
        service.dealRivalAccount(iRequest,list);
        return new ResponseData(list);
    }
}