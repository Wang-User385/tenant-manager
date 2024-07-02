package com.hand.hls.eas.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.eas.dto.HlsCusCheckAccountHistory;
import com.hand.hls.eas.service.IHlsCusCheckAccountHistoryService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class HlsCusCheckAccountHistoryController extends BaseController{

    @Autowired
    private IHlsCusCheckAccountHistoryService service;


    @RequestMapping(value = "/hls/check/account/history/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCheckAccountHistory dto = param.toJavaObject(HlsCusCheckAccountHistory.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/check/account/history/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusCheckAccountHistory> list = param.toJavaList(HlsCusCheckAccountHistory.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/check/account/history/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusCheckAccountHistory> dto = parameter.toJavaList(HlsCusCheckAccountHistory.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


        @RequestMapping(value = "/hls/check/account/data/post")
        @ResponseBody
        public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
            IRequest requestCtx = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusCheckAccountHistory dto = param.toJavaObject(HlsCusCheckAccountHistory.class);
            List<HlsCusCheckAccountHistory> list = new ArrayList<>();
            dto=service.checkAccountDataPost(requestCtx, dto);
            list.add(dto);
            return new ResponseData(list);
        }

    }