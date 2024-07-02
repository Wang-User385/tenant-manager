package com.hand.hls.vat.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.excel.formbean.ExcelBean;
import com.hand.hls.excel.formbean.ExcelExportBean;
import com.hand.hls.excel.service.ExcelExportContainerService;
import com.hand.hls.excel.service.ExcelExportService;
import com.hand.hls.utils.JsonUtils;
import com.hand.hls.vat.dto.HlsCusAcrReceiptHd;
import com.hand.hls.vat.service.IAcrReceiptHdService;
import leaf.bean.LeafRequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class AcrReceiptHdController extends BaseController {

    @Autowired
    private IAcrReceiptHdService service;

    /**
     * 查询收据列表
     *
     * @return
     */
    @RequestMapping(value = "/acr/receipt/hd/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                              HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        Map parameter = (Map) requestData.get("parameter");
        Map condition = JSON.parseObject(JsonUtils.toCamelJsonString(parameter));
        return new ResponseData(service.queryReceiptList(pagenum, pagesize, condition));
    }

    @RequestMapping(value = "/acr/receipt/hd/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusAcrReceiptHd> list = param.toJavaList(HlsCusAcrReceiptHd.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/acr/receipt/hd/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusAcrReceiptHd> dto = parameter.toJavaList(HlsCusAcrReceiptHd.class);
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * 查询待开收据现金流清单
     */
    @RequestMapping("/acr/receipt/queryWaitingReceiptList")
    @ResponseBody
    public ResponseData queryWaitingReceiptList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                                HttpServletRequest request) {
        Map parameter = (Map) requestData.get("parameter");
        Map condition = JSON.parseObject(JsonUtils.toCamelJsonString(parameter));
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        return new ResponseData(service.queryWaitingReceiptList(pagenum, pagesize, condition));
    }

    /**
     * 创建收据
     */
    @RequestMapping("/acr/receipt/hd/create")
    @ResponseBody
    public ResponseData createReceiptList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,String receiptType,
                                          HttpServletRequest request) {
        List<HlsCusAcrReceiptHd> dto = JSON.parseArray(JSON.toJSONString(requestData.get("parameter")), HlsCusAcrReceiptHd.class);
        dto.forEach(item->item.setReceiptType(receiptType));
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        try {
            service.create(iRequest, dto);
        } catch (IllegalArgumentException e) {
            return new ResponseData(false, e.getMessage());
        }
        return new ResponseData();
    }

    /**
     * 收据打印
     */
    @RequestMapping(value = "/vat/acr/receipt/download")
    public ResponseData acrReceiptDownload(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusAcrReceiptHd> hlsCusAcrInvoiceHdList = param.toJavaList(HlsCusAcrReceiptHd.class);

        List<FndAttachment> fndAttachmentList = new ArrayList<>();
        FndAttachment fndAttachment = service.receiptSaveDocAndDownload(hlsCusAcrInvoiceHdList, requestCtx);
        fndAttachmentList.add(fndAttachment);
        return new ResponseData(fndAttachmentList);
    }
}