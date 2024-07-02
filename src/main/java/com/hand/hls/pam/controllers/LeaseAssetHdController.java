package com.hand.hls.pam.controllers;

import com.hand.hls.pam.service.HlsCusLeaseItemService;
import com.hand.hls.pam.service.IHlsCusLeaseItemListService;
import com.hand.hls.pam.util.DateUtil;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.pam.dto.LeaseAssetHd;
import com.hand.hls.pam.service.ILeaseAssetHdService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class LeaseAssetHdController extends BaseController{

    @Autowired
    private ILeaseAssetHdService service;


    @RequestMapping(value = "/lease/asset/hd/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        LeaseAssetHd dto = param.toJavaObject(LeaseAssetHd.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/lease/asset/hd/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<LeaseAssetHd> list = param.toJavaList(LeaseAssetHd.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/lease/asset/hd/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<LeaseAssetHd> dto = parameter.toJavaList(LeaseAssetHd.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


        @RequestMapping(value = "/lease/asset/hd/incept/sumbit")
        @ResponseBody
        public ResponseData conInceptSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, final HttpServletRequest request) throws ParameterNullException, ResMessageException {
            IRequest requestContext = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            //param.put("modify_date", DateUtil.parseDate(param.get("modify_date").toString()));
            LeaseAssetHd leaseAssetHd = param.toJavaObject(LeaseAssetHd.class);


            service.conInceptSubmit(requestContext, leaseAssetHd);
            return new ResponseData();
        }



        @RequestMapping(value = "/lease/confirm/lease/value")
        @ResponseBody
        public ResponseData confirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
            IRequest requestCtx = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            LeaseAssetHd leaseAssetHd = param.toJavaObject(LeaseAssetHd.class);
            return new ResponseData(service.confirm(requestCtx,leaseAssetHd));
        }

        @RequestMapping("/lease/asset/hd/generateAuthorityString")
        public ResponseData generateAuthorityString(HttpServletRequest request) {
            IRequest iRequest = createRequestContext(request);
            String authorityRuleString = service.generateAuthorityString(iRequest);
            return new ResponseData(Arrays.asList(authorityRuleString));
        }

}