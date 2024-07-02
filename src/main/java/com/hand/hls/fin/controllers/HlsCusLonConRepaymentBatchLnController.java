package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatchLn;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchLnService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusLonConRepaymentBatchLnController extends BaseController{

@Autowired
private IHlsCusLonConRepaymentBatchLnService service;


@RequestMapping(value = "/lon/con/repayment/batch/ln/query")
@ResponseBody
public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
    IRequest requestContext = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestContext);
    JSONObject param = (JSONObject) requestData.get("parameter");
    HlsCusLonConRepaymentBatchLn dto = param.toJavaObject(HlsCusLonConRepaymentBatchLn.class);
    return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
}

@RequestMapping(value = "/lon/con/repayment/batch/ln/submit")
@ResponseBody
public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
    IRequest requestCtx = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestCtx);
    JSONArray param = (JSONArray) requestData.get("parameter");
    List<HlsCusLonConRepaymentBatchLn> list = param.toJavaList(HlsCusLonConRepaymentBatchLn.class);
    getValidator().validate(list, result);
    if (result.hasErrors()) {
        ResponseData responseData = new ResponseData(false);
        responseData.setMessage(getErrorMessage(result, request));
        return responseData;
    }
    return new ResponseData(service.batchUpdate(requestCtx, list));
}

@RequestMapping(value = "/lon/con/repayment/batch/ln/remove")
@ResponseBody
public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
    IRequest iRequest = createRequestContext(request);
    RequestHelper.setCurrentRequest(iRequest);
    JSONArray parameter = (JSONArray)requestData.get("parameter");
    List<HlsCusLonConRepaymentBatchLn> dto = parameter.toJavaList(HlsCusLonConRepaymentBatchLn.class);
    service.batchDelete(dto);
    return new ResponseData(dto);
}


@RequestMapping(value = "/lon/con/repayment/ln/account")
@ResponseBody
public ResponseData lonConRepaymentAccount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
    IRequest requestCtx = createRequestContext(request);
    RequestHelper.setCurrentRequest(requestCtx);
    JSONObject param = (JSONObject) requestData.get("parameter");
    HlsCusLonConRepaymentBatchLn hlsCusLonConRepaymentBatchLn = param.toJavaObject(HlsCusLonConRepaymentBatchLn.class);
    List<HlsCusLonConRepaymentBatchLn> hlsCusLonConRepaymentBatchLnList = service.queryWithdrawAccount(hlsCusLonConRepaymentBatchLn);
    return new ResponseData(hlsCusLonConRepaymentBatchLnList);
}


}