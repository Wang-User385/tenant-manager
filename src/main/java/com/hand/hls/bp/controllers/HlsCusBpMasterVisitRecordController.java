package com.hand.hls.bp.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.bp.dto.HlsCusBpMasterVisitRecord;
import com.hand.hls.bp.service.HlsCusIBpMasterVisitRecordService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusBpMasterVisitRecordController extends BaseController {

    @Autowired
    private HlsCusIBpMasterVisitRecordService service;


    @RequestMapping(value = "/hls/bp/master/visit/record/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HlsCusBpMasterVisitRecord dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusBpMasterVisitRecord visitRecord = param .toJavaObject(HlsCusBpMasterVisitRecord.class);
        return new ResponseData(service.select(requestContext, visitRecord, page, pageSize));
    }

    @RequestMapping(value = "/hls/bp/master/visit/record/bpid/query")
    @ResponseBody
    public ResponseData queryByBpid(HlsCusBpMasterVisitRecord dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectByBpid(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/hls/bp/master/visit/record/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusBpMasterVisitRecord> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hls/bp/master/visit/record/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusBpMasterVisitRecord> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}