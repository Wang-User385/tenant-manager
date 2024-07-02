package com.hand.hls.fnd.controllers;

import com.alibaba.fastjson.JSONArray;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.FndBaseRate;
import com.hand.hls.fnd.service.FndBaseRateService;
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
import java.util.Map;



@Controller
public class FndBaseRateController extends BaseController {
    @Autowired
    FndBaseRateService fndBaseRateService;

    public FndBaseRateController() {
    }

    @RequestMapping({"/fct/fnd/baserate/query"})
    @ResponseBody
    public ResponseData selectRate(HttpServletRequest request, String baseRateType, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize) {
        this.createRequestContext(request);
        List<FndBaseRate> datas = this.fndBaseRateService.queryAllByBaseRateType(baseRateType);
        return new ResponseData(datas);
    }

    @RequestMapping({"/fct/fnd/latest/baserate/query"})
    @ResponseBody
    public ResponseData selectLatestRate(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize) {
        Map map = (Map)requestData.get("parameter");
        String baseRateType = (String)map.get("baseRateType");
        List<FndBaseRate> datas = this.fndBaseRateService.queryAllLatestByBaseRateType(baseRateType);
        return new ResponseData(datas);
    }

    @RequestMapping({"/fnd/baserate/query"})
    @ResponseBody
    public ResponseData selectAll(HttpServletRequest request, @RequestParam String base_rate_set, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        List<FndBaseRate> datas = this.fndBaseRateService.queryAllByBaseRateSet(base_rate_set, pagenum, pagesize);
        return new ResponseData(datas);
    }

    @RequestMapping({"/fnd/baserate/submit"})
    @ResponseBody
    public ResponseData submit(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndBaseRate> fndBaseRates = parameter.toJavaList(FndBaseRate.class);
        this.getValidator().validate(fndBaseRates, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest iRequest = this.createRequestContext(request);
            this.fndBaseRateService.batchUpdate(iRequest, fndBaseRates);
            return new ResponseData();
        }
    }

    @RequestMapping({"/fnd/baserate/delete"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<FndBaseRate> fndBaseRates = parameter.toJavaList(FndBaseRate.class);
        this.getValidator().validate(fndBaseRates, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest iRequest = this.createRequestContext(request);
            this.fndBaseRateService.batchUpdate(iRequest, fndBaseRates);
            return new ResponseData(fndBaseRates);
        }
    }
}
