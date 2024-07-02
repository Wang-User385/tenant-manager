package com.hand.hls.fct.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.service.HlsCusFctQuotationCashflowService;
import com.hand.hls.prj.dto.HlsCusPrjProjectInsure;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class HlsCusFctQuotationCashflowController extends BaseController {

    @Autowired
    private HlsCusFctQuotationCashflowService service;


    @RequestMapping(value = "/fct/cashflow/notice/print/query")
    @ResponseBody
    public ResponseData selectNoticePrint(@ModelAttribute(LEAF_PARAM_NAME)LeafRequestData leafRequestData, HttpServletRequest request, HlsCusFctQuotationCashflow dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject)leafRequestData.get("parameter");
        HlsCusFctQuotationCashflow hlsCusFctQuotationCashflow = param.toJavaObject(HlsCusFctQuotationCashflow.class);
        return new ResponseData(service.selectNoticePrint(requestContext, hlsCusFctQuotationCashflow, page, pageSize));
    }

    @RequestMapping(value = "/fct/cashflow/notice/print/file/download")
    @ResponseBody
    public ResponseData downloadNoticePrintFile(@ModelAttribute(LEAF_PARAM_NAME)LeafRequestData leafRequestData,String jsonStr, HttpServletRequest request, HttpServletResponse response) {
        IRequest requestContext = createRequestContext(request);
        JSONArray param = (JSONArray) leafRequestData.get("parameter");
        List<HlsCusFctQuotationCashflow> list = param.toJavaList(HlsCusFctQuotationCashflow.class);
        return service.downloadNoticePrintFile(list, requestContext, request, response);
    }

}
