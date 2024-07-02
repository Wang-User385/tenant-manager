package com.hand.hls.eas.controllers;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.eas.dto.HlsCusCoreAccountHistory;
import com.hand.hls.eas.service.IHlsCusCoreAccountHistoryService;
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
    public class HlsCusCoreAccountHistoryController extends BaseController{

    @Autowired
    private IHlsCusCoreAccountHistoryService service;


    @RequestMapping(value = "/eas/core/account/history/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCoreAccountHistory dto = param.toJavaObject(HlsCusCoreAccountHistory.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/eas/core/account/history/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusCoreAccountHistory> list = param.toJavaList(HlsCusCoreAccountHistory.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/eas/core/account/history/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusCoreAccountHistory> dto = parameter.toJavaList(HlsCusCoreAccountHistory.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }




        /**
         * @Description:对账信息查询
         * @Author: Wangchao
         * @Date: Created om 10:12 2020/5/18
         */
        @RequestMapping(value = "/hls/eas/account/list")
        @ResponseBody
        public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                  HlsCusCoreAccountHistory hlsCusCoreAccountHistory,
                                  @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                  @RequestParam(required = false) String forReverse,
                                  HttpServletRequest request) {
            HlsCusCoreAccountHistory dto = JSON.parseObject(JSON.toJSONString(requestData.get("parameter")), HlsCusCoreAccountHistory.class);
            if(hlsCusCoreAccountHistory.getCheckDate() != null) {
                dto.setCheckDate(hlsCusCoreAccountHistory.getCheckDate());
            }

            IRequest requestContext = createRequestContext(request);
            RequestHelper.setCurrentRequest(requestContext);
            return new ResponseData(service.selectDataByCheckDate(dto, pagenum, pagesize));
        }
}