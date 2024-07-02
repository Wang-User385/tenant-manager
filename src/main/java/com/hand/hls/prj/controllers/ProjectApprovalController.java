package com.hand.hls.prj.controllers;

import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import com.hand.hls.utils.ResMessageException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.prj.dto.PrjProjectApproval;
import com.hand.hls.prj.service.IProjectApprovalService;
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

import java.util.*;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProjectApprovalController extends BaseController {

    @Autowired
    private IProjectApprovalService service;

    @RequestMapping(value = "/prj/project/approval/query/all")
    @ResponseBody
    public ResponseData queryAll(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                 HttpServletRequest request, HttpServletResponse response,
                                 PrjProjectApproval prjProjectApproval,
                                 @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        PrjProjectApproval dto = param.toJavaObject(PrjProjectApproval.class);
        dto.setProjectId(prjProjectApproval.getProjectId());
        //存续期申请和项目尽调共用查询 需要区别参数
        if ("Y".equalsIgnoreCase(prjProjectApproval.getDataClass())) {
            dto.setDataClass("VIRTUAL_CON");
        } else {
            dto.setDataClass("PRJ_PROJECT_INVEST");
        }
        return new ResponseData(service.queryAll(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/prj/project/approval/query/reply/all")
    @ResponseBody
    public ResponseData queryAllReply(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                      HttpServletRequest request, HttpServletResponse response,
                                      PrjProjectApproval prjProjectApproval,
                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        PrjProjectApproval dto = param.toJavaObject(PrjProjectApproval.class);
        dto.setProjectId(prjProjectApproval.getProjectId());
        //存续期申请和项目尽调共用查询 需要区别参数
        if ("Y".equalsIgnoreCase(prjProjectApproval.getDataClass())) {
            dto.setDataClass("VIRTUAL_CON");
        } else {
            dto.setDataClass("PRJ_PROJECT_INVEST");
        }
        return new ResponseData(service.queryAllReply(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/prj/project/approval/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        PrjProjectApproval dto = param.toJavaObject(PrjProjectApproval.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/prj/project/approval/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<PrjProjectApproval> list = param.toJavaList(PrjProjectApproval.class);
        if (list.size() > 0) {
            if (list.get(0).getApprovalId() != null && !list.get(0).getApprovalId().equals("")) {
                list.get(0).set__status("update");
            } else {
                list.get(0).set__status("insert");
            }
        }
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/prj/project/approval/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<PrjProjectApproval> dto = parameter.toJavaList(PrjProjectApproval.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/prj/project/approval/create")
    @ResponseBody
    public ResponseData createApproval(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        String parameter = (String) requestData.get("parameter");
        PrjProjectApproval dto = new PrjProjectApproval();
        dto.setProjectId(parameter);
        PrjProjectApproval p = service.insertSelective(iRequest, dto);
        List<PrjProjectApproval> list = new ArrayList<>();
        list.add(p);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/prj/project/approval/final/submit")
    @ResponseBody
    public ResponseData approvalSubmit(@RequestParam String approvalId,
                                       @RequestParam String meetingId,
                                       @RequestParam String meetingTime,
                                       HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        service.approvalSubmit(requestCtx, approvalId, meetingId, meetingTime);
        return new ResponseData();
    }

    /**
     * 项目上会制作审批通知书审批工作流
     *
     * @param requestData
     * @param pagenum
     * @param pagesize
     * @param request
     * @return
     */
    @RequestMapping(value = "/prj/project/approval/wfl/submit")
    @ResponseBody
    public ResponseData approvalWflSubmit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) throws ResMessageException {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProject> list = param.toJavaList(HlsCusPrjProject.class);
        if (list.size() > 1) {
            throw new ResMessageException("同一时间只能提交一个单据！");
        }
        service.approvalWflSubmit(requestContext, list.get(0), pagenum, pagesize);
        return new ResponseData();
    }

}