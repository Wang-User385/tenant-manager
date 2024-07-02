package com.hand.hls.bp.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.FndScoreResult;
import com.hand.hls.bp.service.IFndScoreResultService;
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
    public class FndScoreResultController extends BaseController{

    @Autowired
    private IFndScoreResultService service;


    @RequestMapping(value = "/fnd/score/result/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        FndScoreResult dto = param.toJavaObject(FndScoreResult.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/fnd/score/result/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<FndScoreResult> list = param.toJavaList(FndScoreResult.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/fnd/score/result/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndScoreResult> dto = parameter.toJavaList(FndScoreResult.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }



        @RequestMapping({"/fnd/score/result/queryById"})
        @ResponseBody
        public ResponseData queryFndScoreResult(FndScoreResult dto, HttpServletRequest request) {
            IRequest iRequest = this.createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            if (dto.getScoreResultId() == null) {
                return new ResponseData(false, "请求参数有误");
            } else {
                List<FndScoreResult> listGather = this.service.queryFndScoreResult(dto.getScoreResultId());
                return new ResponseData(listGather);
            }
        }
}