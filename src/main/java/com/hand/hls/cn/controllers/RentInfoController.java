package com.hand.hls.cn.controllers;

import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import hls.core.utils.exception.HlsCusException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cn.dto.RentInfo;
import com.hand.hls.cn.service.IRentInfoService;
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
    public class RentInfoController extends BaseController{

    @Autowired
    private IRentInfoService service;

    /*租金催收邮件发送*/
    @RequestMapping(value = "/con/rent/email/send")
    @ResponseBody
    public ResponseData conRentSentEmail(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws HlsCusException, com.hand.hls.exception.HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProjectAttachment> attachments = parameter.toJavaList(HlsCusPrjProjectAttachment.class);
        service.sendEmail(requestCtx, attachments);
        return new ResponseData();
    }
    @RequestMapping(value = "/con/rent/info/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        RentInfo dto = param.toJavaObject(RentInfo.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/con/rent/info/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<RentInfo> list = param.toJavaList(RentInfo.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/con/rent/info/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<RentInfo> dto = parameter.toJavaList(RentInfo.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
}