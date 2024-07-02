package com.hand.hls.fnd.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.CalcPrice;
import com.hand.hls.fnd.service.ICalcPriceService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
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
    public class CalcPriceController extends BaseController{

    @Autowired
    private ICalcPriceService service;


    @RequestMapping(value = "/finance/calc/price/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        CalcPrice dto = param.toJavaObject(CalcPrice.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/finance/calc/price/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<CalcPrice> list = param.toJavaList(CalcPrice.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.svaeCalcPrice(requestCtx, list));
    }

    //项目新建报价保存
        @RequestMapping(value = "/prj/calc/price/submit")
        @ResponseBody
        public ResponseData prjUpdate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
            IRequest requestCtx = createRequestContext(request);
            RequestHelper.setCurrentRequest(requestCtx);
            JSONArray param = (JSONArray) requestData.get("parameter");
            List<HlsCusPrjQuotation> list = param.toJavaList(HlsCusPrjQuotation.class);
            return new ResponseData(service.savePrjCalcPrice(requestCtx, list));
            //return new ResponseData();
        }

    @RequestMapping(value = "/finance/calc/price/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<CalcPrice> dto = parameter.toJavaList(CalcPrice.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
}