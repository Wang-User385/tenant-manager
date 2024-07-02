package com.hand.hls.taa.controllers;

import com.hand.hls.pam.dto.LeaseAssetHd;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.taa.dto.JcTransferApplication;
import com.hand.hls.taa.service.IJcTransferApplicationService;
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
    public class JcTransferApplicationController extends BaseController{

    @Autowired
    private IJcTransferApplicationService service;


    @RequestMapping(value = "/jc/transfer/application/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcTransferApplication dto = param.toJavaObject(JcTransferApplication.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/jc/transfer/application/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<JcTransferApplication> list = param.toJavaList(JcTransferApplication.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/jc/transfer/application/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<JcTransferApplication> dto = parameter.toJavaList(JcTransferApplication.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

        @RequestMapping(value = "/jc/transfer/application/approval")
        @ResponseBody
        public ResponseData conInceptSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws ParameterNullException, ResMessageException {
            IRequest requestContext = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            //param.put("modify_date", DateUtil.parseDate(param.get("modify_date").toString()));
            JcTransferApplication jcTransferApplication = param.toJavaObject(JcTransferApplication.class);


            service.conInceptSubmit(requestContext, jcTransferApplication);
            return new ResponseData();
        }
}