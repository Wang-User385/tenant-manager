package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSON;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.fnd.dto.PrjMeetingJudge;
import com.hand.hls.prj.dto.HlsCusPrjProjectBp;
import com.hand.hls.prj.mapper.ProjectMeetingApproverMapper;
import com.hand.hls.utils.JsonUtils;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import com.hand.hls.prj.service.IProjectMeetingApproverService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProjectMeetingApproverController extends BaseController {

    @Autowired
    private IProjectMeetingApproverService service;
    @Autowired
    private ProjectMeetingApproverMapper mapper;

    //添加评委
    @RequestMapping(value = "/prj/project/meeting/approver/add")
    @ResponseBody
    public ResponseData queryProjectBpCount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws InvocationTargetException, IllegalAccessException, IOException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        ProjectMeetingApprover dto = param.toJavaObject(ProjectMeetingApprover.class);
        List<ProjectMeetingApprover> projectMeetingApprovers = service.addApproval(requestCtx, dto);
        return new ResponseData(projectMeetingApprovers);
    }

    //项目批复变更添加评委
    @RequestMapping(value = "/prj/project/meeting/approver/reply/add")
    @ResponseBody
    public ResponseData queryProjectReplyBpCount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws InvocationTargetException, IllegalAccessException, IOException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        ProjectMeetingApprover dto = param.toJavaObject(ProjectMeetingApprover.class);
        List<ProjectMeetingApprover> projectMeetingApprovers = service.addApprovalReply(requestCtx, dto);
        return new ResponseData(projectMeetingApprovers);
    }

    @RequestMapping(value = "/prj/project/meeting/approver/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              ProjectMeetingApprover projectMeetingApprover,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ProjectMeetingApprover dto = param.toJavaObject(ProjectMeetingApprover.class);
        if(param.get("project_id") != null){
            String projectId = param.get("project_id").toString();
            dto.setProjectId(Long.valueOf(projectId));
        }else{
            dto.setProjectId(projectMeetingApprover.getProjectId());
        }
        return new ResponseData(service.queryAll(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/prj/project/meeting/approver/reply/query")
    @ResponseBody
    public ResponseData queryReply(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              ProjectMeetingApprover projectMeetingApprover,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ProjectMeetingApprover dto = param.toJavaObject(ProjectMeetingApprover.class);
        if(param.get("project_id") != null){
            String projectId = param.get("project_id").toString();
            dto.setProjectId(Long.valueOf(projectId));
        }else{
            dto.setProjectId(projectMeetingApprover.getProjectId());
        }
        return new ResponseData(service.queryAllReply(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/prj/project/meeting/approver/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<ProjectMeetingApprover> list = param.toJavaList(ProjectMeetingApprover.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        list.forEach(item -> {
            if(item.getDirectorFlag() != null){
                mapper.updateMeetingJudgeFlag(item);
            }
        });
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/prj/project/meeting/approver/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<ProjectMeetingApprover> dto = parameter.toJavaList(ProjectMeetingApprover.class);
        dto.forEach(item -> {
            item.set__status("delete");
            mapper.deleteMeetingApprover(item);
        });
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/prj/project/meeting/approver/create")
    @ResponseBody
    public ResponseData createApprover(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject parameter = (JSONObject) requestData.get("parameter");
        ProjectMeetingApprover dto = parameter.toJavaObject(ProjectMeetingApprover.class);
        String approvalId = parameter.getString("approval_id");
        service.createApprover(approvalId, iRequest);
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/project/meeting/approver/insert")
    @ResponseBody
    public ResponseData insert(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        String approvalId = (String) requestData.get("approval_id");
        List<PrjMeetingJudge> list = param.toJavaList(PrjMeetingJudge.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        service.insertApprover(requestCtx, list, approvalId);
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/project/meeting/judge/info/query")
    @ResponseBody
    public ResponseData queryInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ProjectMeetingApprover dto = param.toJavaObject(ProjectMeetingApprover.class);
        return new ResponseData(service.queryInfo(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/prj/project/meeting/approver/submit/wfl")
    @ResponseBody
    public ResponseData updateWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ProjectMeetingApprover dto = param.toJavaObject(ProjectMeetingApprover.class);
        dto.set__status("update");
        service.updateByPrimaryKeySelective(requestCtx, dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/prj/project/meeting/approver/judge/query")
    @ResponseBody
    public ResponseData queryProjectJudge(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ProjectMeetingApprover dto = param.toJavaObject(ProjectMeetingApprover.class);
        return new ResponseData(service.queryProjectJudge(requestContext, dto, pagenum, pagesize));
    }
}