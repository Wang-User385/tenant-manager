package com.hand.hls.fnd.controllers;

import com.hand.hls.fnd.mapper.CockpitImportMapper;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.CockpitImport;
import com.hand.hls.fnd.service.ICockpitImportService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class CockpitImportController extends BaseController {

    @Autowired
    private ICockpitImportService service;
    @Autowired
    private CockpitImportMapper mapper;

    @RequestMapping(value = "/jc/cockpit/import/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        CockpitImport dto = param.toJavaObject(CockpitImport.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/jc/cockpit/admin/query")
    @ResponseBody
    public ResponseData queryAdmin(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        CockpitImport dto = param.toJavaObject(CockpitImport.class);
        return new ResponseData(service.queryAdmin(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/jc/cockpit/project/query")
    @ResponseBody
    public ResponseData queryProject(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        CockpitImport dto = param.toJavaObject(CockpitImport.class);
        if (requestContext.getUserId() != null) {
            //添加权限控制 项目经理只能看到自己的 部门负责人看到整个部门的  其他人看到所有的
            dto.setHostProjectManager(requestContext.getUserId());
            CockpitImport cockpitImport = new CockpitImport();
            cockpitImport.setHostProjectManager(requestContext.getUserId());
            String authorizationFlag = mapper.queryPrjAuthorization(cockpitImport).get(0).getAuthorizationFlag();
            dto.setAuthorizationFlag(authorizationFlag);
        }

        return new ResponseData(service.queryProject(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/jc/cockpit/import/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<CockpitImport> list = param.toJavaList(CockpitImport.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/jc/cockpit/import/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<CockpitImport> dto = parameter.toJavaList(CockpitImport.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    // 资金计划日期增导入
    @RequestMapping(value = "/hls/cockpit/import", method = RequestMethod.POST)
    public Map<String, Object> receiptImport(HttpServletRequest request, Long headerId) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            service.receiptImport(iRequest, headerId);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
        }
        return response;
    }
}