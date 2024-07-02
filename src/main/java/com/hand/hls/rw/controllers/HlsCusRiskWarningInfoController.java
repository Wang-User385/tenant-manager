package com.hand.hls.rw.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.rw.dto.HlsCusRiskWarningInfo;
import com.hand.hls.rw.service.HlsCusIRiskWarningInfoService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusRiskWarningInfoController extends BaseController {

    @Autowired
    private HlsCusIRiskWarningInfoService service;


    @RequestMapping(value = "/plm/risk/warning/info/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusRiskWarningInfo dto = param.toJavaObject(HlsCusRiskWarningInfo.class);
        return new ResponseData(service.selectRiskWarningInfo(requestContext, dto));
    }

    @RequestMapping(value = "/plm/risk/warning/info/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusRiskWarningInfo> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/plm/risk/warning/info/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusRiskWarningInfo> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

}