package com.hand.hls.fnd.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.dto.HlsProductDefinitionPara;
import com.hand.hls.fnd.service.IHlsProductDefinitionParaService;
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
public class HlsProductDefinitionParaController extends BaseController {

    @Autowired
    private IHlsProductDefinitionParaService service;


    @RequestMapping(value = "/hls/product/definition/para/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
//        HlsProductDefinitionPara dto = param.toJavaObject(HlsProductDefinitionPara.class);
        HlsProductDefinition hlsProductDefinition = param.toJavaObject(HlsProductDefinition.class);
        //根据产品ID进行查询
        Long definitionId = hlsProductDefinition.getDefinitionId();
        if (definitionId == null) {
            definitionId = 0L;
        }
//        HlsProductDefinitionPara hlsProductDefinitionPara = new HlsProductDefinitionPara();
//        hlsProductDefinitionPara.setDefinitionId(definitionId);
//        return new ResponseData(service.select(requestContext, hlsProductDefinitionPara, pagenum, pagesize));

        hlsProductDefinition = new HlsProductDefinition();
        hlsProductDefinition.setDefinitionId(definitionId);
        return new ResponseData(service.selectHlsProductDefinitionParaList(requestContext, hlsProductDefinition, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/product/definition/para/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsProductDefinitionPara> list = param.toJavaList(HlsProductDefinitionPara.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/product/definition/para/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsProductDefinitionPara> dto = parameter.toJavaList(HlsProductDefinitionPara.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
}