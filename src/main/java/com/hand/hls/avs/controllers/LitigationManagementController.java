package com.hand.hls.avs.controllers;

import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.avs.dto.LitigationManagement;
import com.hand.hls.avs.service.ILitigationManagementService;
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
    public class LitigationManagementController extends BaseController{

    @Autowired
    private ILitigationManagementService service;


    @RequestMapping(value = "/litigation/management/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        LitigationManagement dto = param.toJavaObject(LitigationManagement.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/litigation/management/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<LitigationManagement> list = param.toJavaList(LitigationManagement.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/litigation/management/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<LitigationManagement> dto = parameter.toJavaList(LitigationManagement.class);
        service.batchDelete(dto);
//        JSONObject param = (JSONObject) requestData.get("parameter");
//        LitigationManagement litigationManagement = param.toJavaObject(LitigationManagement.class);
//        service.deleteByPrimaryKey(litigationManagement);
        return new ResponseData(dto);
    }


        @RequestMapping(value = "/litigation/management/approval")
        @ResponseBody
        public ResponseData conInceptSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws ParameterNullException, ResMessageException {
            IRequest requestContext = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            LitigationManagement litigationManagement = param.toJavaObject(LitigationManagement.class);
            service.conInceptSubmit(requestContext, litigationManagement);
            return new ResponseData();
        }
}