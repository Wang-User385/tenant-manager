package com.hand.hls.prj.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.prj.dto.ReplyProductPara;
import com.hand.hls.prj.service.IReplyProductParaService;
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
public class ReplyProductParaController extends BaseController{

@Autowired
private IReplyProductParaService service;


@RequestMapping(value = "/hls/reply/product/para/query")
@ResponseBody
public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestContext);
    JSONObject param = (JSONObject) requestData.get("parameter");
    ReplyProductPara dto = param.toJavaObject(ReplyProductPara.class);
    return new ResponseData(service.query(requestContext,dto,pagenum,pagesize));
}

@RequestMapping(value = "/hls/reply/product/para/submit")
@ResponseBody
public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
    IRequest requestCtx = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestCtx);
    JSONArray param = (JSONArray) requestData.get("parameter");
    List<ReplyProductPara> list = param.toJavaList(ReplyProductPara.class);
    getValidator().validate(list, result);
    if (result.hasErrors()) {
        ResponseData responseData = new ResponseData(false);
        responseData.setMessage(getErrorMessage(result, request));
        return responseData;
    }
    return new ResponseData(service.batchUpdate(requestCtx, list));
}

@RequestMapping(value = "/hls/reply/product/para/remove")
@ResponseBody
public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
    IRequest iRequest = createRequestContext(request);
    RequestHelper.setCurrentRequest(iRequest);
    JSONArray parameter = (JSONArray)requestData.get("parameter");
    List<ReplyProductPara> dto = parameter.toJavaList(ReplyProductPara.class);
    service.batchDelete(dto);
    return new ResponseData(dto);
}

    @RequestMapping(value = "/hls/manufacturer/product/para/query")
    @ResponseBody
    public ResponseData manufacturerQueryProductParaInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                         @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                                         HttpServletRequest request){
        JSONObject param= (JSONObject)requestData.get("parameter");
        IRequest  iRequest=createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        ReplyProductPara dto=param.toJavaObject(ReplyProductPara.class);
        return new ResponseData(service.manufacturerQueryProductParaInfo(iRequest,dto,pagenum,pagesize));
    }
}
