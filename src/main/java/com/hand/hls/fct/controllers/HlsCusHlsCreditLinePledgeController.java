package com.hand.hls.fct.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineGuarantor;
import com.hand.hls.fct.dto.HlsCusHlsCreditLinePledge;
import com.hand.hls.fct.service.HlsCusHlsCreditLineGuarantorService;
import com.hand.hls.fct.service.HlsCusHlsCreditLinePledgeService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusHlsCreditLinePledgeController extends BaseController {

    @Autowired
    private HlsCusHlsCreditLinePledgeService service;

    @Autowired
    private HlsCusHlsCreditLineGuarantorService hlsCusHlsCreditLineGuarantorService;

    @RequestMapping(value = "/hls/credit/line/pledge/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum,
                              @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLinePledge dto = param.toJavaObject(HlsCusHlsCreditLinePledge.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryCreditLinePledge(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/credit/line/guarantor/query/list")
    @ResponseBody
    public ResponseData queryGuarantor(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsCreditLineGuarantor dto = param.toJavaObject(HlsCusHlsCreditLineGuarantor.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(hlsCusHlsCreditLineGuarantorService.queryCreditLineGuarantor(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/credit/line/pledge/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusHlsCreditLinePledge> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hls/credit/line/pledge/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusHlsCreditLinePledge> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}
