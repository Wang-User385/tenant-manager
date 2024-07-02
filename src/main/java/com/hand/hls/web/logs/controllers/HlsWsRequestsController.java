package com.hand.hls.web.logs.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
public class HlsWsRequestsController extends BaseController{

@Autowired
private IHlsWsRequestsService service;


@RequestMapping(value = "/hls/ws/requests/query")
@ResponseBody
public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestContext);
    JSONObject param = (JSONObject) requestData.get("parameter");
    HlsWsRequests dto = param.toJavaObject(HlsWsRequests.class);
    return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
}

@RequestMapping(value = "/hls/ws/requests/submit")
@ResponseBody
public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
    IRequest requestCtx = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestCtx);
    JSONArray param = (JSONArray) requestData.get("parameter");
    List<HlsWsRequests> list = param.toJavaList(HlsWsRequests.class);
    getValidator().validate(list, result);
    if (result.hasErrors()) {
        ResponseData responseData = new ResponseData(false);
        responseData.setMessage(getErrorMessage(result, request));
        return responseData;
    }
    return new ResponseData(service.batchUpdate(requestCtx, list));
}

@RequestMapping(value = "/hls/ws/requests/remove")
@ResponseBody
public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
    IRequest iRequest = createRequestContext(request);
    RequestHelper.setCurrentRequest(iRequest);
    JSONArray parameter = (JSONArray)requestData.get("parameter");
    List<HlsWsRequests> dto = parameter.toJavaList(HlsWsRequests.class);
    service.batchDelete(dto);
    return new ResponseData(dto);
}


@RequestMapping(value = "/r/api/web/interface/requests/save")
@ResponseBody
public ResponseData interfaceSave(HttpServletRequest request,
                                  @RequestBody JSONObject jsonObject) throws Exception {
    IRequest iRequest = createRequestContext(request);

    HlsWsRequests hlsWsRequests = new HlsWsRequests();
    hlsWsRequests.setRequestWsdlUrl("r/api/web/interface/requests/save");
    hlsWsRequests.setRequestJson(jsonObject.toJSONString());

    hlsWsRequests =  service.interfaceSaveAll( hlsWsRequests ,request,iRequest);

    ResponseData responseData = new ResponseData();
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests.setResponsedDate(new Date());
        if(responseData.getMessage() != null) {
            hlsWsRequests.setResponseJson(responseData.getMessage());

        }
    hlsWsRequests =  service.interfaceSave( hlsWsRequests ,iRequest);
    return responseData ;
}
}