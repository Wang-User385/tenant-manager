package com.hand.hls.archive.controllers;

import com.hand.hls.archive.dto.HlsCusArchive;
import hls.core.utils.exception.HlsCusException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.archive.dto.HlsCusArchiveBorrow;
import com.hand.hls.archive.service.HlsCusArchiveBorrowService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class HlsCusArchiveBorrowController extends BaseController {

    @Autowired
    private HlsCusArchiveBorrowService service;


    @RequestMapping(value = "/jc/archive/borrow/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusArchiveBorrow dto = param.toJavaObject(HlsCusArchiveBorrow.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/jc/archive/borrow/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusArchiveBorrow> list = param.toJavaList(HlsCusArchiveBorrow.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/jc/archive/borrow/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusArchiveBorrow> dto = parameter.toJavaList(HlsCusArchiveBorrow.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    //借阅申请
    @RequestMapping(value = "/archive/borrow/approval/submit")
    public ResponseData archiveBorrowSubmit(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusArchiveBorrow dto = param.toJavaObject(HlsCusArchiveBorrow.class);
        service.approvalSubmit(iRequest, dto);
        List<HlsCusArchiveBorrow> list = new ArrayList<>();
        list.add(dto);
        return new ResponseData(list);
    }

    //档案归还
    @RequestMapping(value = "/archive/borrow/return")
    public ResponseData archiveBorrowReturn(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData)  {
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusArchiveBorrow dto = param.toJavaObject(HlsCusArchiveBorrow.class);
        service.archiveBorrowReturn(iRequest, dto);
        List<HlsCusArchiveBorrow> list = new ArrayList<>();
        list.add(dto);
        return new ResponseData(list);
    }

    //校验该项目下的附件类型是否已经归档
    @RequestMapping(value = "/archive/borrow/check")
    public ResponseData archiveBorrowCheck(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData)  throws HlsCusException{
        IRequest iRequest = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusArchive dto = param.toJavaObject(HlsCusArchive.class);

        Boolean result = service.archiveBorrowCheck(iRequest, dto);
        return new ResponseData(result);
    }
}