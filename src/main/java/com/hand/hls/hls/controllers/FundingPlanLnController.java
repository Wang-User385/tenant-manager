package com.hand.hls.hls.controllers;

import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.utils.ResMessageException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.hls.dto.FundingPlanLn;
import com.hand.hls.hls.service.IFundingPlanLnService;
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
    public class FundingPlanLnController extends BaseController{

    @Autowired
    private IFundingPlanLnService service;


    @RequestMapping(value = "/hls/funding/plan/ln/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFundingPlanLn dto = param.toJavaObject(HlsCusFundingPlanLn.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/funding/plan/ln/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusFundingPlanLn> list = param.toJavaList(HlsCusFundingPlanLn.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/funding/plan/ln/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws ResMessageException {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusFundingPlanLn> dto = parameter.toJavaList(HlsCusFundingPlanLn.class);
        service.removePlanLn(iRequest,dto);
        return new ResponseData(dto);
    }

        @RequestMapping(value = "/hls/funding/plan/ln/insertDt")
        @ResponseBody
        public ResponseData insertDt(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            JSONArray parameter = (JSONArray)requestData.get("parameter");
            List<HlsCusFundingPlanLn> dto = parameter.toJavaList(HlsCusFundingPlanLn.class);
            service.insertDt(iRequest,dto);
            return new ResponseData(dto);
        }
}