package com.hand.hls.prj.controllers;

import com.hand.hls.prj.dto.ProjectMeetingApprover;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.prj.dto.ProjectApprovalCondition;
import com.hand.hls.prj.service.ProjectApprovalConditionService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;

import java.util.List;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProjectApprovalConditionController extends BaseController {

    @Autowired
    private ProjectApprovalConditionService service;

    @RequestMapping(value = "/prj/project/approval/condition/query/all1")
    @ResponseBody
    public ResponseData queryAll1(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                  HttpServletRequest request, HttpServletResponse response,
                                  ProjectApprovalCondition projectApprovalCondition,
                                  @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ProjectApprovalCondition dto = param.toJavaObject(ProjectApprovalCondition.class);
        //dto.setProjectId(projectApprovalCondition.getProjectId());
        if("NORMAL".equals(dto.getApprovalDataClass())||dto.getApprovalDataClass() == null){
            return new ResponseData(service.queryAll(requestContext, dto, pagenum, pagesize));
        }else{
            return new ResponseData(service.queryAll2(requestContext, dto, pagenum, pagesize));
        }
    }

    @RequestMapping(value = "/prj/project/approval/condition/query/all2")
    @ResponseBody
    public ResponseData queryAll2(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                  HttpServletRequest request, HttpServletResponse response,
                                  ProjectApprovalCondition projectApprovalCondition,
                                  @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ProjectApprovalCondition dto = param.toJavaObject(ProjectApprovalCondition.class);
        //dto.setProjectId(projectApprovalCondition.getProjectId());
        if("NORMAL".equals(dto.getApprovalDataClass())||dto.getApprovalDataClass() == null){
            return new ResponseData(service.queryAll(requestContext, dto, pagenum, pagesize));
        }else{
            return new ResponseData(service.queryAll2(requestContext, dto, pagenum, pagesize));
        }
    }

    @RequestMapping(value = "/prj/project/approval/condition/query/all3")
    @ResponseBody
    public ResponseData queryAll3(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                  HttpServletRequest request, HttpServletResponse response,
                                  ProjectApprovalCondition projectApprovalCondition,
                                  @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ProjectApprovalCondition dto = param.toJavaObject(ProjectApprovalCondition.class);
        //dto.setProjectId(projectApprovalCondition.getProjectId());
        if("NORMAL".equals(dto.getApprovalDataClass())||dto.getApprovalDataClass() == null){
            return new ResponseData(service.queryAll(requestContext, dto, pagenum, pagesize));
        }else{
            return new ResponseData(service.queryAll2(requestContext, dto, pagenum, pagesize));
        }
    }


    @RequestMapping(value = "/prj/project/approval/condition/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ProjectApprovalCondition dto = param.toJavaObject(ProjectApprovalCondition.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/prj/project/approval/condition/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<ProjectApprovalCondition> list = param.toJavaList(ProjectApprovalCondition.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/prj/project/approval/condition/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<ProjectApprovalCondition> dto = parameter.toJavaList(ProjectApprovalCondition.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
}