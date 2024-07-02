package com.hand.hls.plm.nm.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.nm.dto.PlmNoticeSent;
import com.hand.hls.plm.nm.service.PlmNoticeSentService;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PlmNoticeSentController extends BaseController {

    @Autowired
    private PlmNoticeSentService service;


    @RequestMapping(value = "/plm/notice/sent/query")
    @ResponseBody
    public ResponseData query(PlmNoticeSent dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/notice/sent/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<PlmNoticeSent> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/plm/notice/sent/all/submit")
    @ResponseBody
    public ResponseData creditSave(@ModelAttribute(LEAF_PARAM_NAME)LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);

        JSONArray param  =(JSONArray) requestData.get("parameter");
        List<PlmNoticeSent> dto = param.toJavaList(PlmNoticeSent.class);
        return new ResponseData(service.creditSave(requestCtx, dto.get(0)));
    }

    /*@RequestMapping(value = "/plm/notice/sent/all/notice")
    @ResponseBody
    public ResponseData creditNotice(@RequestBody PlmNoticeSent dto, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.creditNotice(requestCtx, dto));
    }*/

    @RequestMapping(value = "/plm/notice/sent/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<PlmNoticeSent> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/plm/notice/sent/all/query")
    @ResponseBody
    public ResponseData selectNoticeSentAll(PlmNoticeSent dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectNoticeSentAll(requestContext, dto, page, pageSize));
    }


}