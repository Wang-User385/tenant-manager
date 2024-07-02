package com.hand.hls.fp.controllers;

import com.hand.hls.fp.dto.FundFillingReqDetail;
import com.hand.hls.fp.dto.JcFundFillingLn;
import com.hand.hls.fp.service.FundFillingReqDetailService;
import com.hand.hls.fp.service.JcFundFillingLnService;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fp.dto.JcFundFillingDetail;
import com.hand.hls.fp.service.JcFundFillingDetailService;
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
import java.util.Date;
import java.util.List;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class JcFundFillingDetailController extends BaseController {

    @Autowired
    private JcFundFillingDetailService service;
    @Autowired
    private FundFillingReqDetailService reqService;
    @Autowired
    private JcFundFillingLnService serviceLn;

    @RequestMapping(value = "/jc/fund/filling/detail/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFillingDetail dto = param.toJavaObject(JcFundFillingDetail.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    //计划填报明细页面 年 更新头金额
    @RequestMapping(value = "/jc/fund/filling/detail/update")
    @ResponseBody
    public ResponseData updateDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFillingDetail dto = param.toJavaObject(JcFundFillingDetail.class);
        return new ResponseData(service.updateFundLnAmount(requestContext, dto));
    }

    //计划填报明细页面 周计划调整 更新头金额
    @RequestMapping(value = "/jc/fund/filling/detail/req/update")
    @ResponseBody
    public ResponseData updateReqDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        FundFillingReqDetail dto = param.toJavaObject(FundFillingReqDetail.class);
        return new ResponseData(service.updateReqFundLnAmount(requestContext, dto));
    }

    //计划填报明细页面 汇总 年 更新头金额
    @RequestMapping(value = "/jc/fund/filling/detail/summary/update")
    @ResponseBody
    public ResponseData updateSummaryDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFillingDetail dto = param.toJavaObject(JcFundFillingDetail.class);
        return new ResponseData(service.updateSummaryAmount(requestContext, dto));
    }

    @RequestMapping(value = "/jc/fund/filling/detail/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<JcFundFillingDetail> list = param.toJavaList(JcFundFillingDetail.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/jc/fund/filling/detail/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<JcFundFillingDetail> dto = parameter.toJavaList(JcFundFillingDetail.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    //计划填报明细页面 汇总 年 更新头金额
    @RequestMapping(value = "/jc/fund/filling/detail/delete")
    @ResponseBody
    public ResponseData deleteDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFillingDetail dto = param.toJavaObject(JcFundFillingDetail.class);
        service.deleteByPrimaryKey(dto);
        List<JcFundFillingDetail> detailList = new ArrayList<>();
        detailList.add(dto);
        return new ResponseData(detailList);
    }

    //计划填报明细页面 汇总 周计划调整删除 更新头金额
    @RequestMapping(value = "/jc/fund/filling/detail/req/delete")
    @ResponseBody
    public ResponseData deleteReqDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        FundFillingReqDetail dto = param.toJavaObject(FundFillingReqDetail.class);
        reqService.deleteByPrimaryKey(dto);
        List<FundFillingReqDetail> detailList = new ArrayList<>();
        detailList.add(dto);
        return new ResponseData(detailList);
    }
}