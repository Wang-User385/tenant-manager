package com.hand.hls.rpt.controllers;


import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.rpt.dto.RentalIncomeRate;
import com.hand.hls.rpt.service.IRentalIncomeRateService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class RentalIncomeRateController extends BaseController{

    @Autowired
    private IRentalIncomeRateService service;


    @RequestMapping(value = "/rpt/rental/income/rate/queryDetail")
    @ResponseBody
    public ResponseData queryRentalRateDetai(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        RentalIncomeRate dto = param.toJavaObject(RentalIncomeRate.class);
        return new ResponseData(service.queryRentalRateInfo(requestContext,dto,pagenum,pagesize));
    }

    @RequestMapping(value = "/rpt/rental/income/rate/query")
    @ResponseBody
    public ResponseData queryRentalIncomeRate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        RentalIncomeRate dto = param.toJavaObject(RentalIncomeRate.class);
        return new ResponseData(service.queryRentalIncomeRate(requestContext,dto,pagenum,pagesize));
    }



    @RequestMapping(value = "/rpt/rental/income/rate/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData){
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<RentalIncomeRate> dto = parameter.toJavaList(RentalIncomeRate.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    // 租金回收导入
    @RequestMapping(value = "/rpt/rental/income/rateExcelImportConfirm", method = RequestMethod.POST)
    public Map<String, Object> importConfirm(HttpServletRequest request, Long headerId) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            service.IRentalIncomeRateExcelImport(iRequest, headerId);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    //租金回收率汇总
    @RequestMapping(value = "/rpt/rental/income/rate/queryTotal")
    @ResponseBody
    public ResponseData queryRentalIncomeRateTotal(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        RentalIncomeRate dto = param.toJavaObject(RentalIncomeRate.class);
        return new ResponseData(service.rentalIncomeRateTotal(requestContext,dto,pagenum,pagesize));
    }

    //导出查询
    @RequestMapping(value = "rpt/rental/income/rateExcelImportSelect")
    @ResponseBody
    public ResponseData rateExcelImportSelect( HttpServletRequest request, @RequestParam Long headerId) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.service.rateExcelImportSelect(headerId));
    }
}