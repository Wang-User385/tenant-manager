package com.hand.hls.rpt.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.rpt.dto.ReportTemplate;
import com.hand.hls.rpt.service.IReportTemplateService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReportTemplateController extends BaseController {

    @Autowired
    private IReportTemplateService service;

    private final static String RPT009 = "RPT009";
    private final static String RPT010 = "RPT010";
    private static final String AUTHORITY_RULE_FLAG = "authorityRuleFlag";
    private static final String USER_ID = "userId";
    private static final String N = "N";

    @RequestMapping(value = "/rpt/report/template/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ReportTemplate dto = param.toJavaObject(ReportTemplate.class);
        return new ResponseData(service.selectReportTemplateInfo(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/leaf/report/common/query")
    @ResponseBody
    public ResponseData leafCommonQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request, String layout_code, HttpSession session) throws IOException {
        IRequest requestContext = createRequestContext(request);
        Map map = new HashMap();
        if (requestData != null && requestData.getParameter() != null) {
            map = requestData.getParameter();
        }

        if (StringUtils.equals(layout_code, RPT009)||StringUtils.equals(layout_code, RPT010)) {
            requestContext.setAttribute(AUTHORITY_RULE_FLAG, N);
            map.put("user_id",session.getAttribute(USER_ID).toString());
        }
        return new ResponseData(service.leafReportCommonQuery(requestContext, map, pagenum, pagesize, layout_code));
    }

    @RequestMapping(value = "/rpt/report/template/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<ReportTemplate> list = param.toJavaList(ReportTemplate.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.saveTemplate(requestCtx, list));
    }

    @RequestMapping(value = "/rpt/report/template/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<ReportTemplate> dto = parameter.toJavaList(ReportTemplate.class);
        service.removeTemplate(iRequest, dto);
        return new ResponseData(dto);
    }
}