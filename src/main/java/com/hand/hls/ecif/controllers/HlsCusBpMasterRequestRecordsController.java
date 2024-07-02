package com.hand.hls.ecif.controllers;

import com.hand.hls.bp.dto.HlsBpMaster;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.ecif.dto.HlsCusBpMasterRequestRecords;
import com.hand.hls.ecif.service.HlsCusBpMasterRequestRecordsService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class HlsCusBpMasterRequestRecordsController extends BaseController{

    @Autowired
    private HlsCusBpMasterRequestRecordsService service;


    @RequestMapping(value = "/hls/ws/bp/master/request/records/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusBpMasterRequestRecords dto = param.toJavaObject(HlsCusBpMasterRequestRecords.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/ws/bp/master/request/records/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusBpMasterRequestRecords> list = param.toJavaList(HlsCusBpMasterRequestRecords.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/ws/bp/master/request/records/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusBpMasterRequestRecords> dto = parameter.toJavaList(HlsCusBpMasterRequestRecords.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }




        //ECIF接口单笔查询调用
        @RequestMapping(value = "/hls/ws/ecif/single/query")
        @ResponseBody
        public ResponseData wsEcifSingleQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
            IRequest requestCtx = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusBpMasterRequestRecords dto = param.toJavaObject(HlsCusBpMasterRequestRecords.class);
            HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecords = service.wsEcifSignalQuery(requestCtx, dto);
            List<HlsCusBpMasterRequestRecords> list = new ArrayList<>();
            if (hlsCusBpMasterRequestRecords != null) {
                list.add(hlsCusBpMasterRequestRecords);
            }
            return new ResponseData(list);
        }


        //ECIF接口批量创建调用
        @RequestMapping(value = "/hls/ws/ecif/batch/or/update/create")
        @ResponseBody
        public ResponseData ecifBatchCreateUpdate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
            IRequest requestCtx = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusBpMasterRequestRecords dto = param.toJavaObject(HlsCusBpMasterRequestRecords.class);


            HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecords = service.wsEcifBatchCreateUpdate(requestCtx, dto);
            List<HlsCusBpMasterRequestRecords> list = new ArrayList<>();
            if (hlsCusBpMasterRequestRecords != null) {
                list.add(hlsCusBpMasterRequestRecords);
            }
            return new ResponseData(list);
        }


        //单一客户查询  查询的客户系统中已存在则进行覆盖
        @RequestMapping(value = "/hls/ws/ecif/signal/update")
        @ResponseBody
        public ResponseData ecifSignalUpdate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
            IRequest requestCtx = createRequestContext(request);
            JSONObject param = (JSONObject) requestData.get("parameter");
            HlsCusBpMasterRequestRecords dto = param.toJavaObject(HlsCusBpMasterRequestRecords.class);


            HlsCusBpMasterRequestRecords hlsCusBpMasterRequestRecords = service.ecifSignalUpdate(requestCtx, dto);
            List<HlsCusBpMasterRequestRecords> list = new ArrayList<>();
            if (hlsCusBpMasterRequestRecords != null) {
                list.add(hlsCusBpMasterRequestRecords);
            }
            return new ResponseData(list);
        }
}