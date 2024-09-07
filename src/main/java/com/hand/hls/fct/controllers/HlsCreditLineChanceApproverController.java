package com.hand.hls.fct.controllers;

import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.utils.ResMessageException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fct.dto.HlsCreditLineChanceApprover;
import com.hand.hls.fct.service.HlsICreditLineChanceApproverService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;

import java.util.List;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class HlsCreditLineChanceApproverController extends BaseController {

    @Autowired
    private HlsICreditLineChanceApproverService service;


    @RequestMapping(value = "/hls/credit/line/chance/approver/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCreditLineChanceApprover dto = param.toJavaObject(HlsCreditLineChanceApprover.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @Autowired
    private SysUserMapper sysUserMapper;

    @RequestMapping(value = "/hls/credit/line/chance/approver/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCreditLineChanceApprover> list = param.toJavaList(HlsCreditLineChanceApprover.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        HlsCreditLineChanceApprover chanceApprover = list.get(0);
        //chanceApprover.getAllocationId拿到的是userId
        List<SysUser> user = sysUserMapper.findAllocationIdByUserID(chanceApprover.getAllocationId());
        if (user != null) {
            chanceApprover.setApproverId(user.get(0).getAllocationId());
        }
        service.updateByPrimaryKeySelective(requestCtx, chanceApprover);
        return new ResponseData();
    }

    @RequestMapping(value = "/credit/line/chance/approver/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCreditLineChanceApprover> dto = parameter.toJavaList(HlsCreditLineChanceApprover.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


    @RequestMapping(value = "/save/vote")
    @ResponseBody
    public ResponseData saveVote(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCreditLineChanceApprover approver = param.toJavaObject(HlsCreditLineChanceApprover.class);
        service.saveVote(requestCtx,approver);
        return new ResponseData();
    }


}