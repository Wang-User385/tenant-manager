package com.hand.hls.bill.controllers;

import com.alibaba.fastjson.JSON;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bill.dto.hlsPaymentDischarge;
import com.hand.hls.bill.service.IhlsPaymentDischargeService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

    @Controller
    public class hlsPaymentDischargeController extends BaseController{

    @Autowired
    private IhlsPaymentDischargeService service;


    @RequestMapping(value = "/hls/payment/discharge/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        hlsPaymentDischarge dto = param.toJavaObject(hlsPaymentDischarge.class);
        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/hls/payment/discharge/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request){
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<hlsPaymentDischarge> list = param.toJavaList(hlsPaymentDischarge.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/payment/discharge/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<hlsPaymentDischarge> dto = parameter.toJavaList(hlsPaymentDischarge.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

        @RequestMapping("/hls/bill/request/dailyrate/query")
        @ResponseBody
        public ResponseData lonCreditBpLovQuery(hlsPaymentDischarge dto,
                                                @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                                HttpServletRequest request) throws ParseException {
            Map<String, String> parameter = (Map) JSON.parseObject(request.getParameter("_request_data"), Map.class).get("parameter");
            if (StringUtils.isNotBlank(parameter.get("curreny"))) {
                dto.setCurreny(parameter.get("curreny"));
            }
            if (StringUtils.isNotBlank(parameter.get("payment_discharge_date"))) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date paymentDischargeDate = sdf.parse(parameter.get("payment_discharge_date").toString());
                dto.setPaymentDischargeDate(paymentDischargeDate);

            }
            IRequest requestCtx = createRequestContext(request);
            RequestHelper.setCurrentRequest(requestCtx);
            return new ResponseData(service.lonCreditBpLovQuery(dto, pagenum, pagesize));
        }

        @RequestMapping(value = "/hls/payment/discharge/update")
        @ResponseBody
        public void updateStatus(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
            IRequest iRequest = createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            JSONObject param = (JSONObject) requestData.get("parameter");
            hlsPaymentDischarge hlsPaymentDischarges = param.toJavaObject(hlsPaymentDischarge.class);
            service.updateByPrimaryKeySelective(iRequest,hlsPaymentDischarges);
        }


        @RequestMapping(value = "/hls/bill/discharge/sendEmail")
        @ResponseBody
        public ResponseData sendEmail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result,
                                         HttpServletRequest request) {
            IRequest requestCtx = createRequestContext(request);
            RequestHelper.setCurrentRequest(requestCtx);
            JSONObject param = (JSONObject) requestData.get("parameter");
            hlsPaymentDischarge hlsPaymentDischarges = param.toJavaObject(hlsPaymentDischarge.class);

            service.sendEmail(requestCtx, hlsPaymentDischarges);
            return new ResponseData();
        }
}