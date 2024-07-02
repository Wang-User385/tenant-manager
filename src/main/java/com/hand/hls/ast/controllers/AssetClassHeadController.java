package com.hand.hls.ast.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.ast.dto.AssetClassHead;
import com.hand.hls.ast.service.IAssetClassHeadService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
public class AssetClassHeadController extends BaseController {

    @Autowired
    private IAssetClassHeadService service;


    @RequestMapping(value = "/hls/asset/class/head/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        AssetClassHead dto = param.toJavaObject(AssetClassHead.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/asset/class/head/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<AssetClassHead> list = param.toJavaList(AssetClassHead.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/asset/class/head/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<AssetClassHead> dto = parameter.toJavaList(AssetClassHead.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/hls/asset/class/info/create")
    @ResponseBody
    public ResponseData createAssetClassInfo(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws Exception {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        AssetClassHead assetClassHead = param.toJavaObject(AssetClassHead.class);
        return service.createAssetClassHead(iRequest,assetClassHead);
    }
//    public ResponseData createAssetClassInfo(HttpServletRequest request) {
//        IRequest iRequest = createRequestContext(request);
//        RequestHelper.setCurrentRequest(iRequest);
//        return service.createAssetClassInfo(iRequest);
//    }

    @RequestMapping(value = "/hls/asset/class/wfl/submit")
    @ResponseBody
    public ResponseData assetClassWflStart(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        AssetClassHead assetClassHead = param.toJavaObject(AssetClassHead.class);
        return this.service.assetClassWflStart(iRequest,assetClassHead);
    }

    @RequestMapping(value = "/hls/asset/query/id")
    @ResponseBody
    public ResponseData queryId(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        AssetClassHead assetClassHead = param.toJavaObject(AssetClassHead.class);
        Long id=null;
        try{
            id=this.service.queryId(iRequest,assetClassHead);
        }catch(NullPointerException e){
        }
        return  new ResponseData(Collections.singletonList(id));
    }
}