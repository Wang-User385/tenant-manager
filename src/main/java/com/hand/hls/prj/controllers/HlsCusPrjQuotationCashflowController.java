package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.prj.dto.HlsCusPrjProjectInfo;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationCashflowService;
import com.mysql.jdbc.log.LogFactory;
import leaf.bean.LeafRequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HlsCusPrjQuotationCashflowController extends BaseController {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HlsCusPrjQuotationCashflowService service;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper mapper;
    @Autowired
    IExportService excelService;
    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "/ct/prj/quotation/cashflow/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjQuotationCashflow dto = param.toJavaObject(HlsCusPrjQuotationCashflow.class);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryPrjQuotationCashflowByProjectId(dto, pagenum, pageSize));
    }

    @RequestMapping(value = "/ct/prj/quotation/out/cashflow/query")
    @ResponseBody
    public ResponseData queryOutCashflowInfo(HlsCusPrjQuotationCashflow dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryPrjQuotationOutCashflowByProjectId(dto, page, pageSize));
    }

    /**
     * 租赁现金流
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/cus/prj/cash/flow/query")
    @ResponseBody
    public ResponseData prjQueryCashFlow(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjQuotationCashflow dto = param.toJavaObject(HlsCusPrjQuotationCashflow.class);
        return new ResponseData(service.prjQueryCashFlow(dto, page, pageSize));
    }


    @RequestMapping(value = "/ct/prj/project/calc/xirr")
    @ResponseBody
    public ResponseData calcIrrAndXirr(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusPrjProjectInfo dto = new HlsCusPrjProjectInfo();
        dto.setHlsCusPrjQuotation(param.toJavaObject(HlsCusPrjQuotation.class));
        try {
            service.calcIrrAndXirr(requestCtx, dto);
        } catch (Exception e) {
            logger.error("计算出错", e);
            return new ResponseData(false);
        }
        return new ResponseData();
    }

}