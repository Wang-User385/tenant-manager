//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqDt;
import com.hand.hls.csh.service.CshPaymentReqDtService;
import com.hand.hls.utils.ResMessageException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CshPaymentReqDtController extends BaseController {
    @Autowired
    private CshPaymentReqDtService service;

    public CshPaymentReqDtController() {
    }

    @RequestMapping({"/csh/payment/req/dt/query"})
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, HlsCusCshPaymentReqDt hlsCusCshPaymentReqDt, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusCshPaymentReqDt metadataRelation = (HlsCusCshPaymentReqDt)param.toJavaObject(HlsCusCshPaymentReqDt.class);
        if (StringUtils.isEmpty(hlsCusCshPaymentReqDt.getDeductFlag())) {
            hlsCusCshPaymentReqDt.setDeductFlag((String)null);
        }

        return new ResponseData(this.service.queryCshPaymentReqDt(requestContext, hlsCusCshPaymentReqDt, pagenum, pagesize));
    }

    @RequestMapping({"/csh/payment/req/dt/save"})
    @ResponseBody
    public ResponseData save(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, HttpSession session) throws ResMessageException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDtList = parameter.toJavaList(HlsCusCshPaymentReqDt.class);
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        hlsCusCshPaymentReqDtList = this.service.cshPaymentReqDtCreate(requestCtx, hlsCusCshPaymentReqDtList);
        return new ResponseData(hlsCusCshPaymentReqDtList);
    }

    @RequestMapping({"/csh/payment/req/dt/remove"})
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDtList = parameter.toJavaList(HlsCusCshPaymentReqDt.class);
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        this.service.batchDelete(hlsCusCshPaymentReqDtList);
        return new ResponseData(hlsCusCshPaymentReqDtList);
    }
    @RequestMapping({"/csh/payment/req/dt/save/cashflow"})
    @ResponseBody
    public ResponseData saveCashflow(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws ResMessageException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDtList = parameter.toJavaList(HlsCusCshPaymentReqDt.class);
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        this.service.cshPaymentReqDtSave(requestCtx,hlsCusCshPaymentReqDtList);
        return new ResponseData(hlsCusCshPaymentReqDtList);
    }
    @RequestMapping({"/csh/payment/req/dt/delete/cashflow"})
    @ResponseBody
    public ResponseData deleteCashflow(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) throws ResMessageException {
        JSONArray parameter = (JSONArray)requestData.get("parameter");
        List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDtList = parameter.toJavaList(HlsCusCshPaymentReqDt.class);
        IRequest requestCtx = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        this.service.cshPaymentReqDtDelete(requestCtx,hlsCusCshPaymentReqDtList);
        return new ResponseData(hlsCusCshPaymentReqDtList);
    }
}
