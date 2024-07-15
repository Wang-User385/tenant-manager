package com.hand.hls.partner.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hls.partner.dto.UploadAttachList;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.service.IUploadAttachListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
    public ResponseData getUploadUrl(@RequestBody @Valid UploadAttachList uploadAttachList, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return service.getUploadUrl(uploadAttachList,request,iRequest);
    }

}