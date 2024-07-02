package com.hand.hls.fnd.controllers;

import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceAttach;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.FndRiskRatio;
import com.hand.hls.fnd.service.IFndRiskRatioService;
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
import java.util.stream.Collectors;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class FndRiskRatioController extends BaseController{

    @Autowired
    private IFndRiskRatioService service;


    @RequestMapping(value = "/fnd/risk/ratio/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        FndRiskRatio dto = param.toJavaObject(FndRiskRatio.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/fnd/risk/ratio/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<FndRiskRatio> list = param.toJavaList(FndRiskRatio.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        if(list.size() > 0){
            FndRiskRatio queryRatio = new FndRiskRatio();
            queryRatio.setRiskRatioSetCode(list.get(0).getRiskRatioSetCode());
            List<FndRiskRatio> listExist = service.selectSelective(requestCtx,queryRatio);
            for(FndRiskRatio item : list){
                if(item.getRiskRatioId() == null){
                    List<FndRiskRatio> ratios = listExist.stream().filter(ratio -> ratio.getAssetClass().equals(item.getAssetClass())).collect(Collectors.toList());
                    if(ratios.size() > 0 ){
                        throw new HlsCusException("同一资产分类等级仅能对应一条准备金率！");
                    }
                }
            }
        }

        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/fnd/risk/ratio/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndRiskRatio> dto = parameter.toJavaList(FndRiskRatio.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
}