package com.hand.hls.archive.controllers;

import hls.core.utils.exception.HlsCusException;
import com.hand.hls.fin.exception.HlsCusAmountOverException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.archive.dto.HlsCusArchiveAttachment;
import com.hand.hls.archive.service.HlsCusArchiveAttachmentService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class HlsCusArchiveAttachmentController extends BaseController {

    @Autowired
    private HlsCusArchiveAttachmentService service;


    @RequestMapping(value = "/jc/archive/attachment/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusArchiveAttachment dto = param.toJavaObject(HlsCusArchiveAttachment.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/jc/archive/attachment/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusArchiveAttachment> list = param.toJavaList(HlsCusArchiveAttachment.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/jc/archive/attachment/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusArchiveAttachment> dto = parameter.toJavaList(HlsCusArchiveAttachment.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    //档案整理
    @RequestMapping(value = "/archive/attachment/arrangement")
    @ResponseBody
    public ResponseData archiveArrangement(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws HlsCusAmountOverException, HlsCusException {
        IRequest requestContext = createRequestContext(request);

        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusArchiveAttachment> list = param.toJavaList(HlsCusArchiveAttachment.class);

        service.archiveArrangement(requestContext, list);
        return new ResponseData(list);
    }
}