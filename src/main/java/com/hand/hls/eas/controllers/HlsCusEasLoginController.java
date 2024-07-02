package com.hand.hls.eas.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.eas.dto.HlsCusEasLogin;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCusEasLoginController extends BaseController{

@Autowired
private IHlsCusEasLoginService service;


@RequestMapping(value = "/eas/login/query")
@ResponseBody
public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestContext);
    JSONObject param = (JSONObject) requestData.get("parameter");
    HlsCusEasLogin dto = param.toJavaObject(HlsCusEasLogin.class);
    return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
}

@RequestMapping(value = "/eas/login/submit")
@ResponseBody
public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
    IRequest requestCtx = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestCtx);
    JSONArray param = (JSONArray) requestData.get("parameter");
    List<HlsCusEasLogin> list = param.toJavaList(HlsCusEasLogin.class);
    getValidator().validate(list, result);
    if (result.hasErrors()) {
        ResponseData responseData = new ResponseData(false);
        responseData.setMessage(getErrorMessage(result, request));
        return responseData;
    }
    return new ResponseData(service.batchUpdate(requestCtx, list));
}

@RequestMapping(value = "/eas/login/remove")
@ResponseBody
public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
    IRequest iRequest = createRequestContext(request);
    RequestHelper.setCurrentRequest(iRequest);
    JSONArray parameter = (JSONArray)requestData.get("parameter");
    List<HlsCusEasLogin> dto = parameter.toJavaList(HlsCusEasLogin.class);
    service.batchDelete(dto);
    return new ResponseData(dto);
}


    //EAS登录接口
    @RequestMapping(value = "/hls/interface/eas/login")
    @ResponseBody
    public ResponseData easDoLogin(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusEasLogin dto = param.toJavaObject(HlsCusEasLogin.class);
        HlsCusEasLogin hlsCusEasLogin = service.easDoLogin(requestCtx, dto);
        List<HlsCusEasLogin> list = new ArrayList<>();
        if (hlsCusEasLogin != null) {
            list.add(hlsCusEasLogin);
        }
        return new ResponseData(list);
    }
}