package com.hand.hls.vat.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.vat.dto.AcrInvoiceHd;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceLn;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceLn;
import com.hand.hls.vat.exception.AcrInvoiceException;
import com.hand.hls.vat.service.IAcrInvoiceLnService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AcrInvoiceLnController extends BaseController {
    @Autowired
    private IAcrInvoiceLnService service;

    public AcrInvoiceLnController() {
    }

    @RequestMapping({"/acr/invoice/ln/query"})
    @ResponseBody
    public ResponseData query(HlsCusAcrInvoiceLn dto, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping({"/acr/invoice/ln/queryByHdId"})
    @ResponseBody
    public ResponseData queryByHdId(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, @RequestParam Long invoiceHdId, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return new ResponseData(this.service.queryAcrInvoiceLnDetailByHdId(invoiceHdId, pagenum, pagesize));
    }

    @RequestMapping({"/acr/invoice/ln/submit"})
    @ResponseBody
    public ResponseData update(@ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        JSONArray param = (JSONArray)requestData.get("parameter");
        List<HlsCusAcrInvoiceLn> dto = param.toJavaList(HlsCusAcrInvoiceLn.class);
        this.getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest requestCtx = this.createRequestContext(request);
            return new ResponseData(this.service.batchUpdate(requestCtx, dto));
        }
    }

    @RequestMapping({"/acr/invoice/ln/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAcrInvoiceLn> dto) {
        this.service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping({"/acr/invoice/ln/selectForCreate"})
    @ResponseBody
    public ResponseData selectForCreate(HttpServletRequest request, @RequestParam String combineRule, @ModelAttribute("_request_data") LeafRequestData requestData) throws AcrInvoiceException {
        HlsCusAcrInvoiceHd acrInvoiceHd = (HlsCusAcrInvoiceHd) JSON.parseObject(JSON.toJSONString(requestData.get("parameter")), HlsCusAcrInvoiceHd.class);
        String cashflowIds = acrInvoiceHd.getCashflowIds();
        String billingType = acrInvoiceHd.getBillingType();
        if (StringUtils.isEmpty(acrInvoiceHd.getCashflowIds())) {
            return new ResponseData(false, "现金流信息异常!");
        } else {
            String idStrList = "[" + cashflowIds + "]";
            List<Long> idList = JSON.parseArray(idStrList, Long.class);
            IRequest requestContext = this.createRequestContext(request);
            RequestHelper.setCurrentRequest(requestContext);
            return new ResponseData(this.service.selectForCreate(idList, combineRule,billingType));
        }
    }

    @RequestMapping({"/acr/invoice/ln/selectInvoiceBpByContract"})
    @ResponseBody
    public ResponseData selectInvoiceBpByContract(HttpServletRequest request, @RequestParam Long contractId) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return new ResponseData();
    }

    @RequestMapping({"/acr/invoice/ln/selectImportTempList"})
    @ResponseBody
    public ResponseData selectImportTempList(HttpServletRequest request, @RequestParam Long headerId) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.service.selectImportTempList(headerId));
    }

    @RequestMapping({"/acr/invoice/ln/queryTotalAmount"})
    @ResponseBody
    public ResponseData queryTotalAmount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusAcpInvoiceLn hlsCusLonContractWithdraw = param.toJavaObject(HlsCusAcpInvoiceLn.class);
        return new ResponseData(this.service.queryTotalAmount(hlsCusLonContractWithdraw));
    }

    @RequestMapping({"/acr/invoice/ln/queryContractAmount"})
    @ResponseBody
    public ResponseData queryContractAmount(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusAcpInvoiceLn hlsCusLonContractWithdraw = param.toJavaObject(HlsCusAcpInvoiceLn.class);
        return new ResponseData(this.service.queryContractAmount(hlsCusLonContractWithdraw));
    }
}
