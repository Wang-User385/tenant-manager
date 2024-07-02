package com.hand.hls.eas.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.eas.dto.HlsCusPsotEasTmp;
import com.hand.hls.eas.service.IHlsCusPsotEasTmpService;
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
    public class HlsCusPsotEasTmpController extends BaseController{

    @Autowired
    private IHlsCusPsotEasTmpService service;


    @RequestMapping(value = "/gld/psot/eas/tmp/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPsotEasTmp dto = param.toJavaObject(HlsCusPsotEasTmp.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }


    @RequestMapping(value = "/gld/psot/eas/tmp/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPsotEasTmp> list = param.toJavaList(HlsCusPsotEasTmp.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/gld/psot/eas/tmp/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusPsotEasTmp> dto = parameter.toJavaList(HlsCusPsotEasTmp.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


    //凭证传输
    @RequestMapping(value = "/gld/je/data/post")
    @ResponseBody
    public ResponseData gldPost(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPsotEasTmp dto = param.toJavaObject(HlsCusPsotEasTmp.class);
        dto.setSessionId(Long.valueOf(param.get("session_id").toString()));
        HlsCusPsotEasTmp hlsCusPsotEasTmp = service.gldPost(iRequest, dto);
        List<HlsCusPsotEasTmp> list = new ArrayList<>();
        if (hlsCusPsotEasTmp != null) {
            list.add(hlsCusPsotEasTmp);
        }
        return new ResponseData(list);
    }


        //凭证删除
        @RequestMapping(value = "/gld/je/data/delete")
        @ResponseBody
        public ResponseData gldPostDelete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusPsotEasTmp dto = param.toJavaObject(HlsCusPsotEasTmp.class);
            HlsCusPsotEasTmp hlsCusPsotEasTmp = service.gldPostDelete(iRequest, dto);
            List<HlsCusPsotEasTmp> list = new ArrayList<>();
            if (hlsCusPsotEasTmp != null) {
                list.add(hlsCusPsotEasTmp);
            }
            return new ResponseData(list);
        }

}