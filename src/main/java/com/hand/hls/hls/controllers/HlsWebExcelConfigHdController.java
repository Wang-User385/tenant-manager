package com.hand.hls.hls.controllers;

import com.hand.hls.calc.exception.ChangeLimitException;
import com.hand.hls.hls.mapper.HlsWebExcelConfigHdMapper;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.hls.dto.HlsWebExcelConfigHd;
import com.hand.hls.hls.service.IHlsWebExcelConfigHdService;
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
    public class HlsWebExcelConfigHdController extends BaseController{

    @Autowired
    private IHlsWebExcelConfigHdService service;

    @Autowired
    private HlsWebExcelConfigHdMapper configHdMapper;


    @RequestMapping(value = "/hls/web/excel/config/hd/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsWebExcelConfigHd dto = param.toJavaObject(HlsWebExcelConfigHd.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/web/excel/config/hd/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsWebExcelConfigHd> list = param.toJavaList(HlsWebExcelConfigHd.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/web/excel/config/hd/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsWebExcelConfigHd> dto = parameter.toJavaList(HlsWebExcelConfigHd.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/get/configHd/info/by/excel")
    @ResponseBody
    public ResponseData getConfigHdInfoByExcel(HttpServletRequest request,
                                                   @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsWebExcelConfigHd hcc = param.toJavaObject(HlsWebExcelConfigHd.class);
        return new ResponseData(configHdMapper.getConfigHdInfoByExcel(hcc));
    }

    @RequestMapping(value = "/hls/web/excel/config/info/save")
    @ResponseBody
    public ResponseData submit(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData)
            throws ChangeLimitException {
        try {
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            JSONArray param = (JSONArray) requestData.get("parameter");
            HlsWebExcelConfigHd calcConfigs = new HlsWebExcelConfigHd();
            if (param.size() > 0) {
                JSONObject headRecord = param.getJSONObject(0);
                calcConfigs = headRecord.toJavaObject(HlsWebExcelConfigHd.class);
                List<HlsWebExcelConfigHd> lists = service.saveWebExcelConfigHdInfo(iRequest, calcConfigs);
                return new ResponseData(lists);
            } else {
                return new ResponseData();
            }
        } catch (Exception e) {
            throw new ChangeLimitException(e.getMessage());
        }
    }

}