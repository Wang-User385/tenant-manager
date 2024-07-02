package com.hand.hls.hls.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.hls.dto.HlsDurationDeposit;
import com.hand.hls.hls.service.HlsDurationDepositService;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsDurationDepositController extends BaseController {

    @Autowired
    private HlsDurationDepositService service;


    @RequestMapping(value = "/hls/duration/deposit/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationDeposit dto = param.toJavaObject(HlsDurationDeposit.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/duration/deposit/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsDurationDeposit> list = param.toJavaList(HlsDurationDeposit.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.executeDeposit(requestCtx, list));
    }

    @RequestMapping(value = "/hls/duration/deposit/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsDurationDeposit> dto = parameter.toJavaList(HlsDurationDeposit.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


    @RequestMapping(value = "/hls/duration/deposit/management/query")
    @ResponseBody
    public ResponseData depositManagementQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDurationDeposit dto = param.toJavaObject(HlsDurationDeposit.class);
        return new ResponseData(service.depositManagementQuery(dto, pagenum, pagesize));
    }


}