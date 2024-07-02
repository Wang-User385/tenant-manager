package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.prj.dto.HlsCusPrjProjectInsure;
import com.hand.hls.prj.service.HlsCusPrjProjectInsureService;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusPrjProjectInsureController extends BaseController {

    @Autowired
    private HlsCusPrjProjectInsureService service;


    @RequestMapping(value = "/prj/project/insure/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectInsure dto = param.toJavaObject(HlsCusPrjProjectInsure.class);
        List<HlsCusPrjProjectInsure> list = service.select(requestContext, dto, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/prj/project/insure/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProjectInsure> list = param.toJavaList(HlsCusPrjProjectInsure.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/prj/project/insure/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusPrjProjectInsure> list = param.toJavaList(HlsCusPrjProjectInsure.class);
        service.batchDelete(list);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/prj/project/contract/insure/query")
    @ResponseBody
    public ResponseData queryContractInsureInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectInsure dto = param.toJavaObject(HlsCusPrjProjectInsure.class);
        List<HlsCusPrjProjectInsure> list = service.queryContractInsureInfo(requestContext, dto, pagenum, pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/prj/project/insure/approve/wfl")
    @ResponseBody
    public ResponseData insureApproveWfl(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectInsure dto = param.toJavaObject(HlsCusPrjProjectInsure.class);
        service.insureApproveWfl(requestCtx,dto);

        return new ResponseData();
    }
}