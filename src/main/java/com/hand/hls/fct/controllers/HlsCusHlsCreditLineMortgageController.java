package com.hand.hls.fct.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineMortgage;
import com.hand.hls.fct.service.HlsCusHlsCreditLineMortgageService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusHlsCreditLineMortgageController extends BaseController {

    @Autowired
    private HlsCusHlsCreditLineMortgageService service;


    @RequestMapping(value = "/hls/credit/line/mortgage/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum,
                              @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLineMortgage dto = param.toJavaObject(HlsCusHlsCreditLineMortgage.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryCreditLineMortgage(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/credit/line/mortgage/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusHlsCreditLineMortgage> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hls/credit/line/mortgage/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusHlsCreditLineMortgage> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}
