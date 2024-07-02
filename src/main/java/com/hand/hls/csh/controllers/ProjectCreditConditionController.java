package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSON;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.utils.DocumentValidate;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import com.hand.hls.csh.service.IProjectCreditConditionService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProjectCreditConditionController extends BaseController {

    @Autowired
    private IProjectCreditConditionService service;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;


    @RequestMapping(value = "/prj/project/credit/condition/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ProjectCreditCondition dto = param.toJavaObject(ProjectCreditCondition.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/prj/project/credit/condition/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<ProjectCreditCondition> list = param.toJavaList(ProjectCreditCondition.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        if(list.size() != 0){
            return new ResponseData(service.batchUpdate(requestCtx, list));
        }else {
            return new ResponseData();
        }
    }

    @RequestMapping(value = "/prj/project/credit/condition/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<ProjectCreditCondition> dto = parameter.toJavaList(ProjectCreditCondition.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/csh/condition/exempt/wfl")
    @ResponseBody
    public ResponseData submitWfl(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws Exception {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        JSONArray recArr = (JSONArray)param.get("rec_arr");
        List<String> creditConditionIds = JSON.parseArray(JSON.toJSONString(recArr),String.class);

        service.submitWfl(session,iRequest,creditConditionIds);
        return new ResponseData(true);
    }

    @RequestMapping(value = "/csh/condition/exempt/node/save")
    @ResponseBody
    public ResponseData conditionNodeSave(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject parameter = (JSONObject) requestData.get("parameter");

        String creditConditionIds = (String)parameter.get("creditConditionIds");
        String endTaskNodeId = (String) parameter.get("endTaskNodeId");
        List<String> list = Arrays.asList(creditConditionIds.split(","));
        list.forEach(item->{
            ProjectCreditCondition projectCreditCondition = new ProjectCreditCondition();
            projectCreditCondition.setCreditConditionId(Long.valueOf(item));
            projectCreditCondition.setEndTaskNodeId(endTaskNodeId);
            projectCreditCondition.set__status("update");
            service.updateByPrimaryKeySelective(requestContext,projectCreditCondition);
        });
        return new ResponseData();
    }
}