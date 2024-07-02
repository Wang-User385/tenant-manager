package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fin.dto.HlsCusCtLonContractMortgage;
import com.hand.hls.fin.service.HlsCusCtLonContractMortgageService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusCtLonContractMortgageController extends BaseController {

    @Autowired
    private HlsCusCtLonContractMortgageService service;


    //HlsCusCtLonContractMortgage dto,
    @RequestMapping(value = "/hlsLon/contract/mortgage/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusCtLonContractMortgage dto = param.toJavaObject(HlsCusCtLonContractMortgage.class);

        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/hlsLon/contract/mortgage/submit")
    @ResponseBody
    public ResponseData defaultUpdate(@RequestBody List<HlsCusCtLonContractMortgage> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hlsLon/contract/mortgage/remove")
    @ResponseBody
    public ResponseData defaultDelete(HttpServletRequest request, @RequestBody List<HlsCusCtLonContractMortgage> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}