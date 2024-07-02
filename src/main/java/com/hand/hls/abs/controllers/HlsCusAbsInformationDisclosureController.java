package com.hand.hls.abs.controllers;

import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsInformationDisclosure;
import com.hand.hls.abs.service.HlsCusAbsInformationDisclosureService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class HlsCusAbsInformationDisclosureController extends BaseController{

    @Autowired
    private HlsCusAbsInformationDisclosureService service;


    @RequestMapping(value = "/ct/abs/information/disclosure/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusAbsInformationDisclosure dto = param.toJavaObject(HlsCusAbsInformationDisclosure.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/ct/abs/information/disclosure/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusAbsInformationDisclosure> list = param.toJavaList(HlsCusAbsInformationDisclosure.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/ct/abs/information/disclosure/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusAbsInformationDisclosure> dto = parameter.toJavaList(HlsCusAbsInformationDisclosure.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/ct/abs/information/disclosure/create")
    @ResponseBody
    public ResponseData ctAbsInformationDisclosureCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws Exception {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusAbsInformationDisclosure dto = param.toJavaObject(HlsCusAbsInformationDisclosure.class);

        HlsCusAbsInformationDisclosure newRecord = service.ctAbsInformationDisclosureCreate(requestContext, dto);
        List<HlsCusAbsInformationDisclosure> absInformationDisclosures = new ArrayList<>();
        absInformationDisclosures.add(newRecord);
        return new ResponseData(absInformationDisclosures);
    }

    /**
     * 信息披露申请提交审批
     * @param requestData
     * @param request
     * @return
     */
    @RequestMapping(value = "submit/information/disclosure/wfl")
    @ResponseBody
    public ResponseData submitInformationDisclosureWfl(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusAbsInformationDisclosure dto = param.toJavaObject(HlsCusAbsInformationDisclosure.class);

        service.submitInformationDisclosureWfl(requestContext,dto);
        List<HlsCusAbsInformationDisclosure> absInformationDisclosures = new ArrayList<>();
        absInformationDisclosures.add(dto);
        return new ResponseData(absInformationDisclosures);
    }
}