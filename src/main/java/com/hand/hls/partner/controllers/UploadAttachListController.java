package com.hand.hls.partner.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.partner.service.IUploadAttachListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

    @Controller
    @RequestMapping(value = {"/r/api"})
    public class UploadAttachListController extends BaseController{

    @Autowired
    private IUploadAttachListService service;

    @RequestMapping(
                value = {"/di/getUploadUrl"},
                method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject getUploadUrl(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        IRequest iRequest = createRequestContext(request);
        return service.getUploadUrl(jsonObject,request,iRequest);
    }

    @RequestMapping(
            value = {"/di/upload"},
            method = {RequestMethod.PUT})
    @ResponseBody
    public JSONObject upload(@RequestBody JSONObject jsonObject,@RequestParam("fileId")String fileId, HttpServletRequest request) throws Exception {
        return null;
//        IRequest iRequest = createRequestContext(request);
//        return service.getUploadUrl(jsonObject,request,iRequest);
    }

}