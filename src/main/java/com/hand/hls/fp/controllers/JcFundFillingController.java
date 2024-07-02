package com.hand.hls.fp.controllers;

import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fp.dto.JcFundFillingLn;
import com.hand.hls.fp.mapper.JcFundFillingLnMapper;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.service.JcFundFillingService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class JcFundFillingController extends BaseController {

    @Autowired
    private JcFundFillingService service;
    @Autowired
    private JcFundFillingLnMapper lnMapper;


    @RequestMapping(value = "/jc/fund/filling/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/jc/fund/filling/query/All/By/Uint")
    @ResponseBody
    public ResponseData queryAll(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        String unitId = requestContext.getAttributeMap().get("unitId").toString();
        String userId = requestContext.getAttributeMap().get("user_id").toString();
        dto.setUnitId(Long.valueOf(unitId));
        dto.setCreatedBy(Long.valueOf(userId));
        return new ResponseData(service.queryAllByUnit(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/jc/fund/filling/Summary/query/All/By/Uint")
    @ResponseBody
    public ResponseData querySummaryAll(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        String unitId = requestContext.getAttributeMap().get("unitId").toString();
        String userId = requestContext.getAttributeMap().get("user_id").toString();
        return new ResponseData(service.querySummaryAllByUnit(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/jc/fund/filling/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<JcFundFilling> list = param.toJavaList(JcFundFilling.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/jc/fund/filling/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<JcFundFilling> dto = parameter.toJavaList(JcFundFilling.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    //计划填报明细页面 年 初始化查询
    @RequestMapping(value = "/jc/fund/filling/year/init/query")
    @ResponseBody
    public ResponseData queryDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        return new ResponseData(service.initYear(requestContext, dto));
    }

    //计划填报明细页面 年 初始化查询
    @RequestMapping(value = "/jc/fund/filling/quarter/init/query")
    @ResponseBody
    public ResponseData queryQuarterDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        return new ResponseData(service.initQuarter(requestContext, dto));
    }

    //计划填报明细页面 月 初始化查询
    @RequestMapping(value = "/jc/fund/filling/mon/init/query")
    @ResponseBody
    public ResponseData queryMonDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        return new ResponseData(service.initMon(requestContext, dto));
    }

    //计划填报明细页面 月 初始化查询
    @RequestMapping(value = "/jc/fund/filling/mon/prompt/query")
    @ResponseBody
    public ResponseData queryMonPrompt(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        JcFundFillingLn ln = new JcFundFillingLn();
        if (dto.getFillYear() != null && dto.getFillYear() != "") {
            ln.setFillYear(dto.getFillYear());
        }
        if (dto.getFillMon() != null && dto.getFillMon() != "") {
            ln.setFillMon(dto.getFillMon());
        }
        if (dto.getFillWeek() != null && dto.getFillWeek() != "") {
            ln.setFillWeek(dto.getFillWeek());
        }
        return new ResponseData(lnMapper.queryFiledPrompt(ln));
    }

    //计划填报明细页面 周 初始化查询
    @RequestMapping(value = "/jc/fund/filling/week/init/query")
    @ResponseBody
    public ResponseData queryWeekDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        return new ResponseData(service.initWeek(requestContext, dto));
    }

    //计划填报明细页面 周 初始化查询
    @RequestMapping(value = "/jc/fund/filling/week/prompt/query")
    @ResponseBody
    public ResponseData queryWeekPrompt(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        JcFundFillingLn ln = new JcFundFillingLn();
        if (dto.getFillYear() != null && dto.getFillYear() != "") {
            ln.setFillYear(dto.getFillYear());
        }
        if (dto.getFillMon() != null && dto.getFillMon() != "") {
            ln.setFillMon(dto.getFillMon());
        }
        if (dto.getFillWeek() != null && dto.getFillWeek() != "") {
            ln.setFillWeek(dto.getFillWeek());
        }
        return new ResponseData(lnMapper.queryWeekFiledPrompt(ln));
    }

    //工作流提交 FUNDING_PLAN_WFL_NEW
    @RequestMapping(value = "/jc/fund/filling/submit/wfl")
    @ResponseBody
    public ResponseData fillingSubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling fundFilling = new JcFundFilling();
        fundFilling = service.fillingSubmitWfl(requestCtx, param.toJavaObject(JcFundFilling.class));
        return new ResponseData(true);
    }

    //工作流提交 FUNDING_PLAN_WFL_NEW
    @RequestMapping(value = "/jc/fund/filling/summary/submit/wfl")
    @ResponseBody
    public ResponseData fillingSummarySubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling fundFilling = new JcFundFilling();
        fundFilling = service.fillingSummarySubmitWfl(requestCtx, param.toJavaObject(JcFundFilling.class));
        return new ResponseData(true);
    }

    //汇总 计划填报明细页面 年 初始化查询
    @RequestMapping(value = "/jc/fund/filling/summary/year/init/query")
    @ResponseBody
    public ResponseData querySummaryYearDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        return new ResponseData(service.initSummaryYear(requestContext, dto));
    }

    //汇总 计划填报明细页面 年 初始化查询
    @RequestMapping(value = "/jc/fund/filling/summary/quarter/init/query")
    @ResponseBody
    public ResponseData querySummaryQuarterDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        return new ResponseData(service.initSummaryQuarter(requestContext, dto));
    }

    //汇总  计划填报明细页面 月 初始化查询
    @RequestMapping(value = "/jc/fund/filling/summary/mon/init/query")
    @ResponseBody
    public ResponseData querySummaryMonDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        return new ResponseData(service.initSummaryMon(requestContext, dto));
    }


    //汇总  计划填报明细页面 周 初始化查询
    @RequestMapping(value = "/jc/fund/filling/summary/week/init/query")
    @ResponseBody
    public ResponseData querySummaryWeekDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        return new ResponseData(service.initSummaryWeek(requestContext, dto));
    }

    @RequestMapping(value = "/jc/fund/filling/query/All/By/Uint/Req")
    @ResponseBody
    public ResponseData queryAllReq(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling dto = param.toJavaObject(JcFundFilling.class);
        String unitId = requestContext.getAttributeMap().get("unitId").toString();
        String userId = requestContext.getAttributeMap().get("user_id").toString();
        dto.setUnitId(Long.valueOf(unitId));
        dto.setCreatedBy(Long.valueOf(userId));
        return new ResponseData(service.queryAllByUnitReq(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/jc/fund/filling/req/create")
    @ResponseBody
    public ResponseData createRateLnContract(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, JcFundFilling fundFillingReq) throws Exception {
        IRequest requestContext = this.createRequestContext(request);

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<JcFundFilling> reqList = parameter.toJavaList(JcFundFilling.class);

        ResponseData responseData = new ResponseData(true);
        try {
            JcFundFilling fillingReq = this.service.createReq(requestContext, reqList.get(0));
            responseData.setRows(Collections.singletonList(fillingReq));
        } catch (Exception var9) {
            responseData.setSuccess(false);
            responseData.setMessage(var9.getMessage());
        }
        return responseData;
    }

    @RequestMapping(value = "/jc/fund/filling/req/submit/wfl")
    @ResponseBody
    public ResponseData fillingReqSubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JcFundFilling fundFilling = new JcFundFilling();
        fundFilling = service.fillingReqSubmitWfl(requestCtx, param.toJavaObject(JcFundFilling.class));
        return new ResponseData(true);
    }
}