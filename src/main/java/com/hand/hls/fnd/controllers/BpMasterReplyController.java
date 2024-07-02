package com.hand.hls.fnd.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.prj.dto.BpMasterReply;
import com.hand.hls.prj.mapper.BpMasterReplyMapper;
import com.hand.hls.prj.service.IBpMasterReplyService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class BpMasterReplyController extends BaseController{

@Autowired
private IBpMasterReplyService service;
@Autowired
private BpMasterReplyMapper mapper;


@RequestMapping(value = "/hls/bp/master/reply/query")
@ResponseBody
public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestContext);
    JSONObject param = (JSONObject) requestData.get("parameter");
    BpMasterReply dto = param.toJavaObject(BpMasterReply.class);
    return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
}

@RequestMapping(value = "/hls/bp/master/reply/submit")
@ResponseBody
public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
    IRequest requestCtx = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestCtx);
    JSONArray param = (JSONArray) requestData.get("parameter");
    List<BpMasterReply> list = param.toJavaList(BpMasterReply.class);
    getValidator().validate(list, result);
    if (result.hasErrors()) {
        ResponseData responseData = new ResponseData(false);
        responseData.setMessage(getErrorMessage(result, request));
        return responseData;
    }
    return new ResponseData(service.batchUpdate(requestCtx, list));
}

@RequestMapping(value = "/hls/bp/master/reply/remove")
@ResponseBody
public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
    IRequest iRequest = createRequestContext(request);
    RequestHelper.setCurrentRequest(iRequest);
    JSONArray parameter = (JSONArray)requestData.get("parameter");
    List<BpMasterReply> dto = parameter.toJavaList(BpMasterReply.class);
    service.batchDelete(dto);
    return new ResponseData(dto);
}

@RequestMapping(value = "/hls/bp/master/factory/reply/query")
@ResponseBody
public ResponseData factoryQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestContext);
    JSONObject param = (JSONObject) requestData.get("parameter");
    HlsProductDefinition dto = new HlsProductDefinition();

    String manufacturerId = String.valueOf(param.get("manufacturer_id"));
    String dealerId = String.valueOf(param.get("dealer_id"));

    return new ResponseData(service.selectReplyInfo(requestContext,manufacturerId,dealerId));
}

@RequestMapping(value = "/hls/bp/manufacturer/reply/query")
@ResponseBody
public ResponseData manufacturerQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestContext);
    JSONObject param = (JSONObject) requestData.get("parameter");
    BpMasterReply dto = param.toJavaObject(BpMasterReply.class);
    return new ResponseData(service.manufacturerQuery(requestContext,dto,pagenum,pagesize));
}
}