package com.hand.hls.prj.controllers;

import com.alibaba.druid.util.StringUtils;
import com.hand.hls.prj.service.IReplyProductService;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.prj.dto.ReplyProduct;
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
public class ReplyProductController extends BaseController {

    @Autowired
    private IReplyProductService service;


    @RequestMapping(value = "/hls/reply/product/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        ReplyProduct dto = param.toJavaObject(ReplyProduct.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/reply/product/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<ReplyProduct> list = param.toJavaList(ReplyProduct.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/reply/product/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<ReplyProduct> dto = parameter.toJavaList(ReplyProduct.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/hls/manufacturer/reply/product/query")
    @ResponseBody
    public ResponseData manufacturerBpCreditLineQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                      @RequestParam(defaultValue = "") String latestFlag,
                                                      @RequestParam(required = false) Long replyId,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                                      HttpServletRequest request) {

        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        ReplyProduct dto = param.toJavaObject(ReplyProduct.class);

//        if (!StringUtils.isEmpty(latestFlag)) {
//            dto.setLatestFlag(latestFlag);
//        }
        if (!StringUtils.isEmpty(replyId.toString())) {
            dto.setReplyId(replyId);
        }
        return new ResponseData(service.manufacturerQueryProductInfo(iRequest, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/manufacturer/reply/product/query1")
    @ResponseBody
    public ResponseData manufacturerBpCreditLineQuery1(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                      @RequestParam(defaultValue = "") String latestFlag,
                                                      @RequestParam(required = false) Long replyId,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                                      HttpServletRequest request) {

        JSONObject param = (JSONObject) requestData.get("parameter");
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        ReplyProduct dto = param.toJavaObject(ReplyProduct.class);

//        if (!StringUtils.isEmpty(latestFlag)) {
//            dto.setLatestFlag(latestFlag);
//        }
//        if (!StringUtils.isEmpty(replyId.toString())) {
//            dto.setReplyId(replyId);
//        }
        return new ResponseData(service.manufacturerQueryProductInfo1(iRequest, dto, pagenum, pagesize));
    }
}