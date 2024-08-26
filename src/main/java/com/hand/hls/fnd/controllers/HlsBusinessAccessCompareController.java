package com.hand.hls.fnd.controllers;

import com.hand.hls.fnd.mapper.HlsBusinessAccessCompareMapper;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.HlsBusinessAccessCompare;
import com.hand.hls.fnd.service.IHlsBusinessAccessCompareService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
    public class HlsBusinessAccessCompareController extends BaseController{

    @Autowired
    private IHlsBusinessAccessCompareService service;
    @Autowired
    private HlsBusinessAccessCompareMapper mapper;


    @RequestMapping(value = "/hls/business/access/compare/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsBusinessAccessCompare dto = param.toJavaObject(HlsBusinessAccessCompare.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
        //List<HlsBusinessAccessCompare> list = mapper.queryAll();
        //return new ResponseData(list);
    }

    @RequestMapping(value = "/hls/business/access/compare/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsBusinessAccessCompare> list = param.toJavaList(HlsBusinessAccessCompare.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/business/access/compare/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsBusinessAccessCompare> dto = parameter.toJavaList(HlsBusinessAccessCompare.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
}