package com.hand.hls.bill.controllers;

import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.bill.service.IhlsBillRequestService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class hlsBillRequestController extends BaseController{

    @Autowired
    private IhlsBillRequestService service;


    @RequestMapping(value = "/hls/bill/request/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        hlsBillRequest dto = param.toJavaObject(hlsBillRequest.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }


    @RequestMapping(value = "/hls/bill/request/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<hlsBillRequest> dto = parameter.toJavaList(hlsBillRequest.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
    @RequestMapping(value = "/hls/bill/request/submit")
    @ResponseBody
    public ResponseData submit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ParameterNullException, ResMessageException {


        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        hlsBillRequest hlsBillRequest = param.toJavaObject(hlsBillRequest.class);
        return new ResponseData(service.insureSubmit(iRequest,hlsBillRequest));
    }


    @RequestMapping(value = "/hls/bill/discharge/update")
    @ResponseBody
    public void updateStatus(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        hlsBillRequest hlsBillRequest = param.toJavaObject(hlsBillRequest.class);
        service.updateByPrimaryKeySelective(iRequest,hlsBillRequest);
    }

    @RequestMapping(value = "/hls/bill/discharge/updateData")
    @ResponseBody
    public ResponseData bpAssetsInit(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result,
                                     HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        hlsBillRequest hlsBillRequest = param.toJavaObject(hlsBillRequest.class);

        service.bpAssetsInit(requestCtx, hlsBillRequest);
        return new ResponseData();
    }

}