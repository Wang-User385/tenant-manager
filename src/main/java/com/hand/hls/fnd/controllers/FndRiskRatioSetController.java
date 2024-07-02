package com.hand.hls.fnd.controllers;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.exception.BaseException;
import com.hand.hls.fnd.mapper.FndRiskRatioSetMapper;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.FndRiskRatioSet;
import com.hand.hls.fnd.service.IFndRiskRatioSetService;
import com.hand.hls.fnd.service.IFndRiskRatioService;
import com.hand.hls.fnd.mapper.FndRiskRatioMapper;
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
public class FndRiskRatioSetController extends BaseController {

    @Autowired
    private IFndRiskRatioSetService service;
    @Autowired
    private FndRiskRatioSetMapper mapper;
    @Autowired
    private IFndRiskRatioService fndRiskRatioService;
    @Autowired
    private FndRiskRatioMapper fndRiskRatioMapper;


    @RequestMapping(value = "/fnd/risk/ratio/set/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        FndRiskRatioSet dto = param.toJavaObject(FndRiskRatioSet.class);
        PageHelper.startPage(pagenum, pagesize);
        return new ResponseData(mapper.query(dto));
    }

    @RequestMapping(value = "/fnd/risk/ratio/set/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<FndRiskRatioSet> list = param.toJavaList(FndRiskRatioSet.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/fnd/risk/ratio/set/remove")
    @ResponseBody
    public ResponseData remove(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<FndRiskRatioSet> dto = parameter.toJavaList(FndRiskRatioSet.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = {"/fnd/risk/ratio/set/delete"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndRiskRatioSet> fndRiskRatioSetList = parameter.toJavaList(FndRiskRatioSet.class);
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        service.batchDeleteLine(fndRiskRatioSetList);
        service.batchDelete(fndRiskRatioSetList);
        return new ResponseData(fndRiskRatioSetList);
    }

    @RequestMapping({"/fnd/riskRatioSet/copy"})
    @ResponseBody
    public ResponseData copyHeadAndLine(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<FndRiskRatioSet> fndRiskRatioSets = parameter.toJavaList(FndRiskRatioSet.class);
        this.getValidator().validate(fndRiskRatioSets, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest iRequest = this.createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            service.copyHeadAndLine(iRequest,fndRiskRatioSets);

            return new ResponseData();
        }
    }

    @RequestMapping({"/fnd/riskRatioSet/save"})
    @ResponseBody
    public ResponseData saveHeadAndLine(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndRiskRatioSet> fndRiskRatioSets = parameter.toJavaList(FndRiskRatioSet.class);
        this.getValidator().validate(fndRiskRatioSets, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest iRequest = this.createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            service.saveHeadAndLine(iRequest,fndRiskRatioSets);

            return new ResponseData();
        }
    }
}