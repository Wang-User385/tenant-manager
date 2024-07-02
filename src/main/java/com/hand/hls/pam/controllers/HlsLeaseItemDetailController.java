package com.hand.hls.pam.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.pam.dto.HlsLeaseItemDetail;
import com.hand.hls.pam.service.IHlsLeaseItemDetailService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class HlsLeaseItemDetailController extends BaseController{

    @Autowired
    private IHlsLeaseItemDetailService service;


    @RequestMapping(value = "/hls/lease/item/detail/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsLeaseItemDetail dto = param.toJavaObject(HlsLeaseItemDetail.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/lease/item/detail/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsLeaseItemDetail> list = param.toJavaList(HlsLeaseItemDetail.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/lease/item/detail/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsLeaseItemDetail> dto = parameter.toJavaList(HlsLeaseItemDetail.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


        // 租赁物新增导入
        @RequestMapping(value = "/hls/pledge/dongcan/list/import", method = RequestMethod.POST)
        public Map<String, Object> receiptImportPledgeDc(HttpServletRequest request, Long headerId, Long leaseItemId, String patternDet) throws IOException {
            IRequest iRequest = createRequestContext(request);
            Map<String, Object> response = new HashMap<String, Object>();
            response.put("success", false);
            try {
                service.receiptImportPledgeDc(iRequest, headerId,leaseItemId,patternDet);
                response.put("message", "导入成功");
                response.put("success", true);
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "导入失败！" + e.getMessage());
            }
            return response;
        }


}