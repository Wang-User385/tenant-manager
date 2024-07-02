package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fin.dto.HlsCusLonContractBail;
import com.hand.hls.fin.service.HlsCusLonContractBailService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusLonContractBailController extends BaseController {

    @Autowired
    private HlsCusLonContractBailService service;


    //HlsCusLonContractBail dto,
    @RequestMapping(value = "/hlsLon/contract/bail/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractBail dto = param.toJavaObject(HlsCusLonContractBail.class);

        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/hlsLon/contract/bail/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusLonContractBail> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hlsLon/contract/bail/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusLonContractBail> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}