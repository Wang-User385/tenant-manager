package com.hand.hls.GENER.controllers;

import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.utils.ResMessageException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.GENER.dto.HlsGeneralIssue;
import com.hand.hls.GENER.service.IHlsGeneralIssueService;
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

import java.util.Arrays;
import java.util.List;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class HlsGeneralIssueController extends BaseController {

    @Autowired
    private IHlsGeneralIssueService service;
    @Autowired
    FndCompanyMapper fndCompanyMapper;


    @RequestMapping(value = "/hls/general/issue/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsGeneralIssue dto = param.toJavaObject(HlsGeneralIssue.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/general/issue/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsGeneralIssue> list = param.toJavaList(HlsGeneralIssue.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/general/issue/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsGeneralIssue> dto = parameter.toJavaList(HlsGeneralIssue.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


    @RequestMapping(value = "/hls/general/issue/wfl")
    @ResponseBody
    public ResponseData SubmitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsGeneralIssue dto = param.toJavaObject(HlsGeneralIssue.class);
        IRequest requestCtx = createRequestContext(request);
        service.SubmitWfl(requestCtx, dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/hls/general/generateAuthorityString")
    @ResponseBody
    public ResponseData AuthorityString(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws ResMessageException {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsGeneralIssue dto = param.toJavaObject(HlsGeneralIssue.class);
        IRequest requestCtx = createRequestContext(request);
        Long companyId = requestCtx.getCompanyId();
        FndCompany company = fndCompanyMapper.selectByPrimaryKey(companyId);
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        String unitCode = (String) session.getAttribute("unitCode");
        String positionCode = (String) session.getAttribute("positionCode");
        String empCode = requestCtx.getEmployeeCode();
        String authorityRuleString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + positionCode + '"' + "." + '"' + empCode + '"';
        return new ResponseData(Arrays.asList(authorityRuleString));
    }


    @RequestMapping(value = "/hls/general/issue/update")
    @ResponseBody
    public void updateHlsGeneralIssue(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsGeneralIssue hlsgeneralissue = param.toJavaObject(HlsGeneralIssue.class);
        service.updateByPrimaryKeySelective(iRequest, hlsgeneralissue);

    }
}