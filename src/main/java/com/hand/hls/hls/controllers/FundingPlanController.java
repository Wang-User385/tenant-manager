package com.hand.hls.hls.controllers;

import com.hand.hls.hls.dto.HlsCusFundingPlan;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.hls.dto.FundingPlan;
import com.hand.hls.hls.service.IFundingPlanService;
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
    public class FundingPlanController extends BaseController{

    @Autowired
    private IFundingPlanService service;


    @RequestMapping(value = "/hls/funding/plan/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusFundingPlan dto = param.toJavaObject(HlsCusFundingPlan.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/funding/plan/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusFundingPlan> list = param.toJavaList(HlsCusFundingPlan.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/funding/plan/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws ResMessageException {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusFundingPlan> dto = parameter.toJavaList(HlsCusFundingPlan.class);
        service.removePlan(iRequest,dto);
        return new ResponseData(dto);
    }
        @RequestMapping(value = "/hls/funding/plan/create")
        @ResponseBody
        public ResponseData createFundingPlan(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
            //创建虚拟合同
            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusFundingPlan dto = param.toJavaObject(HlsCusFundingPlan.class);
            IRequest requestCtx = createRequestContext(request);
            List<HlsCusFundingPlan> list = new ArrayList<>();
            list.add(service.createFundingPlan(requestCtx, dto));
            return new ResponseData(list);
        }
        @RequestMapping(value = "/hls/funding/plan/approve/submit")
        @ResponseBody
        public ResponseData submit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException, ParameterNullException {
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);

            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusFundingPlan hlsCusFundingPlan = param.toJavaObject(HlsCusFundingPlan.class);
            return new ResponseData(service.fundingPlanSubmit(iRequest, hlsCusFundingPlan));
        }

        @RequestMapping(value = "/hls/funding/plan/select/plan")
        @ResponseBody
        public ResponseData selectPlan(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException, ParameterNullException {
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);

            JSONArray jsonArray = (JSONArray)requestData.get("parameter");
            List<HlsCusFundingPlanLn> hlsCusFundingPlanLns = jsonArray.toJavaList(HlsCusFundingPlanLn.class);
            return new ResponseData(service.selectPlan(iRequest, hlsCusFundingPlanLns));
        }

        @RequestMapping(value = "/hls/funding/plan/approve/check")
        @ResponseBody
        public ResponseData check(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException, ParameterNullException {
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);

            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusFundingPlan hlsCusFundingPlan = param.toJavaObject(HlsCusFundingPlan.class);
            service.fundingPlanCheck(iRequest, hlsCusFundingPlan);
            return new ResponseData();
        }

        @RequestMapping(value = "/hls/funding/plan/save")
        @ResponseBody
        public ResponseData save(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException, ParameterNullException {
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);

            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusFundingPlan hlsCusFundingPlan = param.toJavaObject(HlsCusFundingPlan.class);
            service.saveFundingPlan(iRequest, hlsCusFundingPlan);
            return new ResponseData();
        }
    }