package com.hand.hls.common.controllers;

import com.github.pagehelper.PageHelper;
import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;
import com.hand.hls.common.mapper.HlsCusDocumentRecordListMapper;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.common.dto.HlsCusDocumentRecordList;
import com.hand.hls.common.service.HlsCusDocumentRecordListService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
public class HlsCusDocumentRecordListController extends BaseController{

@Autowired
private HlsCusDocumentRecordListService service;

    @Autowired
    private HlsCusDocumentRecordListMapper hlsCusDocumentRecordListMapper;


@RequestMapping(value = "/hls/ws/document/record/list/query")
@ResponseBody
public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestContext);
    JSONObject param = (JSONObject) requestData.get("parameter");
    HlsCusDocumentRecordList dto = param.toJavaObject(HlsCusDocumentRecordList.class);
    return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
}

@RequestMapping(value = "/hls/ws/document/record/list/submit")
@ResponseBody
public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
    IRequest requestCtx = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestCtx);
    JSONArray param = (JSONArray) requestData.get("parameter");
    List<HlsCusDocumentRecordList> list = param.toJavaList(HlsCusDocumentRecordList.class);
    getValidator().validate(list, result);
    if (result.hasErrors()) {
        ResponseData responseData = new ResponseData(false);
        responseData.setMessage(getErrorMessage(result, request));
        return responseData;
    }
    return new ResponseData(service.batchUpdate(requestCtx, list));
}

@RequestMapping(value = "/hls/ws/document/record/list/remove")
@ResponseBody
public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
    IRequest iRequest = createRequestContext(request);
    RequestHelper.setCurrentRequest(iRequest);
    JSONArray parameter = (JSONArray)requestData.get("parameter");
    List<HlsCusDocumentRecordList> dto = parameter.toJavaList(HlsCusDocumentRecordList.class);
    service.batchDelete(dto);
    return new ResponseData(dto);
}


    // 通过outboundId查询
    @RequestMapping(value = "/hls/ws/document/record/by/outboundId/query")
    @ResponseBody
    public ResponseData selectDocumentByOutboundId(HlsCusDocumentRecordList dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        PageHelper.startPage(page, pageSize);
        return new ResponseData(hlsCusDocumentRecordListMapper.selectDocumentByOutboundId(dto));
    }


    //接口统一重发逻辑处理
    @RequestMapping(value = "/hls/ws/interface/unified/resend")
    @ResponseBody
    public ResponseData wsInterfaceUnifiedResend(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHapInterfaceOutbound dto = param.toJavaObject(HlsCusHapInterfaceOutbound.class);

        HlsCusHapInterfaceOutbound hapInterfaceOutbound = service.wsInterfaceUnifiedResend(requestCtx, dto);
        List<HlsCusHapInterfaceOutbound> list = new ArrayList<>();
        if (hapInterfaceOutbound != null) {
            list.add(hapInterfaceOutbound);
        }
        return new ResponseData(list);
    }
}