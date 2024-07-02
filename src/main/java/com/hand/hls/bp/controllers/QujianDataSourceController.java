package com.hand.hls.bp.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.QujianDataSource;
import com.hand.hls.bp.service.IQujianDataSourceService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.List;
import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class QujianDataSourceController extends BaseController{

    @Autowired
    private IQujianDataSourceService service;


    @RequestMapping(value = "/qujian/data/source/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        QujianDataSource dto = param.toJavaObject(QujianDataSource.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/qujian/data/source/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<QujianDataSource> list = param.toJavaList(QujianDataSource.class);

        /*for(QujianDataSource qujianDataSource:list){
           qujianDataSource.setExecllentValue(chager(qujianDataSource.getExecllentValue()));
           qujianDataSource.setGoodValue(chager(qujianDataSource.getGoodValue()));
           qujianDataSource.setAverageValue(chager(qujianDataSource.getAverageValue()));
           qujianDataSource.setPoorValue(chager(qujianDataSource.getPoorValue()));
        }*/


        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/qujian/data/source/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<QujianDataSource> dto = parameter.toJavaList(QujianDataSource.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }


        @RequestMapping(value = "/qujian/data/source/findList")
        @ResponseBody
        public ResponseData selectQujianDataSourceList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request){
            IRequest requestContext = createRequestContext(request);
            RequestHelper.setCurrentRequest(requestContext);
            JSONObject param = (JSONObject) requestData.get("parameter");
            QujianDataSource dto = param.toJavaObject(QujianDataSource.class);
            return new ResponseData(service.selectQujianDataSource(requestContext,dto,pagenum,pagesize));
        }

        public String chager(String str){
            String value=str.replace("%","");

            BigDecimal oldValue = new BigDecimal(value);
            BigDecimal result52 = oldValue.divide(BigDecimal.valueOf(100),5,BigDecimal.ROUND_HALF_UP);
            return String.valueOf(result52);
        }
}