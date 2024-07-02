package com.hand.hls.pam.controllers;

import com.hand.hls.pam.dto.HlsCusHlsLeaseItemInsure;
import com.hand.hls.pam.dto.HlsCusLeaseItemManage;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.pam.dto.HlsLeaseItemInsure;
import com.hand.hls.pam.service.HlsCusHlsLeaseItemInsureService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    public class HlsCusHlsLeaseItemInsureController extends BaseController{

    @Autowired
    private HlsCusHlsLeaseItemInsureService service;


    @RequestMapping(value = "/hls/lease/item/insure/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsLeaseItemInsure dto = param.toJavaObject(HlsCusHlsLeaseItemInsure.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/lease/item/insure/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
     /*   IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusHlsLeaseItemInsure> list = param.toJavaList(HlsCusHlsLeaseItemInsure.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));*/


        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest requestCtx = createRequestContext(request);
        HlsCusHlsLeaseItemInsure dto = param.toJavaObject(HlsCusHlsLeaseItemInsure.class);
        List<HlsCusHlsLeaseItemInsure> list = new ArrayList<>(1);
        if(dto.getInsureId() == null || dto.getInsureId() ==0){
            list.add(service.insertSelective(requestCtx, dto));
        }else{
            list.add(service.updateByPrimaryKeySelective(requestCtx, dto));
        }

        return new ResponseData(list);

    }

    @RequestMapping(value = "/hls/lease/item/insure/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusHlsLeaseItemInsure> dto = parameter.toJavaList(HlsCusHlsLeaseItemInsure.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
}