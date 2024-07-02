package com.hand.hls.fnd.controllers;

import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import hls.core.utils.exception.HlsCusException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.FndExchangeRate;
import com.hand.hls.fnd.service.IFndExchangeRateService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class FndExchangeRateController extends BaseController {

    @Autowired
    private IFndExchangeRateService service;


    @RequestMapping(value = "/fnd/exchange/rate/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        FndExchangeRate dto = param.toJavaObject(FndExchangeRate.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/fnd/exchange/rate/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<FndExchangeRate> list = param.toJavaList(FndExchangeRate.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/fnd/exchange/rate/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<FndExchangeRate> dto = parameter.toJavaList(FndExchangeRate.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/fnd/exchange/rate/gld/query")
    @ResponseBody
    public ResponseData queryForGld(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");

        FndExchangeRate dto = new FndExchangeRate();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dto.setExchangeDate(df.parse(param.get("jeDate").toString()));
            dto.setForeignCurrency(param.get("currency").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseData(service.selectSelective(requestContext, dto));
    }



    @RequestMapping(value = "/fnd/enchange/query/rate")
    @ResponseBody
    public ResponseData queryExchangeRate(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws HlsCusException {

        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        FndExchangeRate fndExchangeRate= param.toJavaObject(FndExchangeRate.class);

        Double exchangeRate = service.selectExchangeRate(requestCtx,fndExchangeRate);
        fndExchangeRate.setExchangeRate(exchangeRate);
        List<FndExchangeRate> list = new ArrayList();
        list.add(fndExchangeRate);
        return new ResponseData(list);
    }


}