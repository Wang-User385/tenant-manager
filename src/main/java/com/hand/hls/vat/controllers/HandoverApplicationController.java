package com.hand.hls.vat.controllers;

import com.hand.hls.fin.exception.HlsCusAmountOverException;
import hls.core.utils.exception.HlsCusException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.vat.dto.HandoverApplication;
import com.hand.hls.vat.service.IHandoverApplicationService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.validation.BindingResult;

import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class HandoverApplicationController extends BaseController{

    @Autowired
    private IHandoverApplicationService service;


    @RequestMapping(value = "/invoice/handover/application/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HandoverApplication dto = param.toJavaObject(HandoverApplication.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/invoice/handover/application/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HandoverApplication> list = param.toJavaList(HandoverApplication.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/invoice/handover/application/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HandoverApplication> dto = parameter.toJavaList(HandoverApplication.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }




        @RequestMapping(value = "/invoice/handover/application/newCreate")
        @ResponseBody
        public void newCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws HlsCusAmountOverException, HlsCusException {
            IRequest requestContext = createRequestContext(request);

            JSONObject param = (JSONObject)requestData.get("parameter");
            HandoverApplication handoverapplication =param.toJavaObject(HandoverApplication.class);

            service.newCreate(requestContext, handoverapplication);

        }

        @RequestMapping(value = "/invoice/handover/application/Newupdate")
        @ResponseBody
        public ResponseData newUpdate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request) {
            IRequest requestCtx = createRequestContext(request);

            JSONArray param = (JSONArray)requestData.get("parameter");
            List<HandoverApplication> dto= param.toJavaList(HandoverApplication.class);
            List<HandoverApplication> hlsCusAcrInvoiceHdList =  service.updateHandoverStatus(requestCtx, dto);
            return new ResponseData(hlsCusAcrInvoiceHdList);
        }


        @RequestMapping(value = "/invoice/handover/application/confirm/update")
        @ResponseBody
        public ResponseData updateInvoiceConfirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request) {
            IRequest requestCtx = createRequestContext(request);

            JSONArray param = (JSONArray)requestData.get("parameter");
            List<HandoverApplication> dto= param.toJavaList(HandoverApplication.class);
            List<HandoverApplication> hlsCusAcrInvoiceHdList =  service.updateHandoverConfirmStatus(requestCtx, dto);
            return new ResponseData(hlsCusAcrInvoiceHdList);
        }
        @RequestMapping(value = "/invoice/handover/application/reject/update")
        @ResponseBody
        public ResponseData updateInvoiceReject(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request) {
            IRequest requestCtx = createRequestContext(request);

            JSONArray param = (JSONArray)requestData.get("parameter");
            List<HandoverApplication> dto= param.toJavaList(HandoverApplication.class);
            List<HandoverApplication> hlsCusAcrInvoiceHdList = service.updateHandoverRejectStatus(requestCtx, dto);
            return new ResponseData(hlsCusAcrInvoiceHdList);
        }
    }