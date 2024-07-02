package com.hand.hls.vat.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.sys.utils.OracleUtils;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceLn;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import com.hand.hls.vat.exception.AcpInvoiceException;
import com.hand.hls.vat.mapper.HlsCusAcpInvoiceLnMapper;
import com.hand.hls.vat.service.IAcpInvoiceLnService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AcpInvoiceLnController extends BaseController {
    @Autowired
    private IAcpInvoiceLnService service;

    @Autowired
    private HlsCusAcpInvoiceLnMapper acpInvoiceLnMapper;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    public AcpInvoiceLnController() {
    }

    @RequestMapping({"/acp/invoice/ln/query"})
    @ResponseBody
    public ResponseData query(HlsCusAcpInvoiceLn dto, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping({"/acp/invoice/ln/submit"})
    @ResponseBody
    public ResponseData update(@ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws AcpInvoiceException {
        List<HlsCusAcpInvoiceLn> dto = JSON.parseArray(JSON.toJSONString(requestData.get("parameter")), HlsCusAcpInvoiceLn.class);
        this.getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            IRequest requestCtx = this.createRequestContext(request);

            this.service.submit(requestCtx, dto);
            return new ResponseData();
        }
    }

    @RequestMapping({"/acp/invoice/ln/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws AcpInvoiceException {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray)requestData.get("parameter");
        List<HlsCusAcpInvoiceLn> dto = param.toJavaList(HlsCusAcpInvoiceLn.class);
        this.service.delete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping({"/acp/invoice/ln/queryAcpInvoiceDetail"})
    @ResponseBody
    public ResponseData queryAcpInvoiceDetail(@ModelAttribute("_request_data") LeafRequestData requestData,
                                              @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest iRequest = this.createRequestContext(request);
        iRequest.setAttribute("wflRuleControlFlag", "Y");
        RequestHelper.setCurrentRequest(iRequest);
        HlsCusAcpInvoiceLn dto = JSON.parseObject(JSON.toJSONString(requestData.get("parameter")), HlsCusAcpInvoiceLn.class);
        JSONObject param = (JSONObject)requestData.get("parameter");
        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }

        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum,pagesize);
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }

        List<HlsCusAcpInvoiceLn> list=acpInvoiceLnMapper.queryAcpInvoiceDetail(dto);
        return new ResponseData(list);

    }

    @RequestMapping({"/search/invoice/ln/home/query"})
    @ResponseBody
    public ResponseData searchInvoiceLnHomeQuery(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        JSONObject param = (JSONObject)requestData.get("parameter");
        if (param == null) {
            return new ResponseData(false, "请求参数缺失!");
        } else {
            IRequest iRequest = this.createRequestContext(request);
            RequestHelper.setCurrentRequest(iRequest);
            HlsCusAcpInvoiceLn dto = (HlsCusAcpInvoiceLn)param.toJavaObject(HlsCusAcpInvoiceLn.class);
            return new ResponseData(this.service.searchInvoiceLnHomeQuery(iRequest, dto, pagenum, pagesize));
        }
    }

    @RequestMapping({"/acp/invoice/ln/selectInvoiceKind"})
    @ResponseBody
    public ResponseData selectInvoiceKind(HttpServletRequest request) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.service.selectInvoiceKind());
    }

    @RequestMapping({"/acp/invoice/ln/selectImportTempList"})
    @ResponseBody
    public ResponseData selectImportTempList(HttpServletRequest request, @RequestParam Long headerId) {
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(this.service.selectImportTempList(headerId));
    }

    @RequestMapping({"/acp/invoice/ln/confirmImport"})
    @ResponseBody
    public ResponseData confirmImport(@ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws AcpInvoiceException {
        JSONArray param = (JSONArray)requestData.get("parameter");
        IRequest requestCtx = this.createRequestContext(request);
        List<HlsCusAcpInvoiceLn> dto = param.toJavaList(HlsCusAcpInvoiceLn.class);
        for (HlsCusAcpInvoiceLn hlsCusAcpInvoiceLn : dto) {
            HlsCusConContract contract = new HlsCusConContract();
            contract.setContractNumber(hlsCusAcpInvoiceLn.getContractIdN());
            contract = hlsCusConContractMapper.selectOne(contract);
            hlsCusAcpInvoiceLn.setContractId(contract.getContractId());
            Double netPrice = CalculateUtil.div(hlsCusAcpInvoiceLn.getTotalAmount(), CalculateUtil.add(1D, Double.valueOf(hlsCusAcpInvoiceLn.getTaxTypeRateN())));
            Double taxAmount = CalculateUtil.sub(hlsCusAcpInvoiceLn.getTotalAmount(), netPrice);
            hlsCusAcpInvoiceLn.setNetPrice(netPrice);
            hlsCusAcpInvoiceLn.setTaxAmount(taxAmount);
        }
        this.getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(this.getErrorMessage(result, request));
            return responseData;
        } else {
            this.service.importConfirm(requestCtx, dto);
            return new ResponseData();
        }
    }

    @RequestMapping({"/acp/invoice/ln/confirmInvoice"})
    @ResponseBody
    public ResponseData updateCheckStatus(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws AcpInvoiceException {
        List<HlsCusAcpInvoiceLn> dto = JSON.parseArray(JSON.toJSONString(requestData.get("parameter")), HlsCusAcpInvoiceLn.class);
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        this.service.confirm(iRequest, dto);
        return new ResponseData(true, "确认成功!");
    }

    @RequestMapping({"/acp/invoice/ln/reverse"})
    @ResponseBody
    public ResponseData invoiceLnReverse(HttpServletRequest request, @RequestBody HlsCusAcpInvoiceLn dto) throws AcpInvoiceException {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        this.service.reverse(iRequest, dto);
        return new ResponseData(true, "反冲成功!");
    }
}