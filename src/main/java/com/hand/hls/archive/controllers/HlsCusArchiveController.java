package com.hand.hls.archive.controllers;


import com.hand.hls.exception.HlsCusException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.archive.dto.HlsCusArchive;
import com.hand.hls.archive.service.HlsCusArchiveService;
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
public class HlsCusArchiveController extends BaseController {

    @Autowired
    private HlsCusArchiveService service;


    @RequestMapping(value = "/jc/archive/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusArchive dto = param.toJavaObject(HlsCusArchive.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/jc/archive/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusArchive> list = param.toJavaList(HlsCusArchive.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/jc/archive/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusArchive> dto = parameter.toJavaList(HlsCusArchive.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    /**
     * 档案归集
     *
     * @param requestData
     * @param request
     * @return
     */
    @RequestMapping(value = "/archive/pooling")
    @ResponseBody
    public ResponseData archivePooling(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws Exception {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusArchive dto = param.toJavaObject(HlsCusArchive.class);
        List<HlsCusArchive> hlsCusArchives = new ArrayList<>();
        hlsCusArchives = service.archivePooling(requestContext, dto);

        return new ResponseData(hlsCusArchives);
    }

    //归档确认
    @RequestMapping(value = "/archive/confirm")
    public ResponseData archiveConfirm(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusArchive> dto = parameter.toJavaList(HlsCusArchive.class);
        service.archiveConfirm(iRequest, dto);
        return new ResponseData(dto);
    }
}