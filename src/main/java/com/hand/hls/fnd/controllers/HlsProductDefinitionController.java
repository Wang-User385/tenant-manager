package com.hand.hls.fnd.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.service.IHlsProductDefinitionService;
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
public class HlsProductDefinitionController extends BaseController {

    @Autowired
    private IHlsProductDefinitionService service;


    @RequestMapping(value = "/hls/product/definition/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsProductDefinition dto = param.toJavaObject(HlsProductDefinition.class);
//        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
        return new ResponseData(service.selectHlsProductDefinitionList(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/product/definition/save")
    @ResponseBody
    public ResponseData save(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsProductDefinition> list = param.toJavaList(HlsProductDefinition.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdateProductDefinition(requestCtx, list));
    }

    /*@RequestMapping(value = "/hls/product/definition/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsProductDefinition> dto = parameter.toJavaList(HlsProductDefinition.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }*/

    @RequestMapping(value = "/hls/product/definition/submit")
    @ResponseBody
    public ResponseData submitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsProductDefinition hlsProductDefinition = param.toJavaObject(HlsProductDefinition.class);
        getValidator().validate(hlsProductDefinition, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }

        hlsProductDefinition = service.submitHlsProductDefinition(requestCtx, hlsProductDefinition);
        List<HlsProductDefinition> hlsProductDefinitionList = new ArrayList<>();
        hlsProductDefinitionList.add(hlsProductDefinition);

        return new ResponseData(hlsProductDefinitionList);
    }

    @RequestMapping(value = "/hls/product/definition/change")
    @ResponseBody
    public ResponseData change(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsProductDefinition hlsProductDefinition = param.toJavaObject(HlsProductDefinition.class);
        getValidator().validate(hlsProductDefinition, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }

        hlsProductDefinition = service.changeHlsProductDefinition(requestCtx, hlsProductDefinition);
        List<HlsProductDefinition> hlsProductDefinitionList = new ArrayList<>();
        hlsProductDefinitionList.add(hlsProductDefinition);

        return new ResponseData(hlsProductDefinitionList);
    }
}