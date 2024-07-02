package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.CshBaseDto;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.service.CshTransactionRefundService;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.ResMessageException;
import leaf.bean.LeafRequestData;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/4/24
 * @description: 收款退款
 */
@Controller
public class CshTransactionRefundController extends BaseController {

    @Resource
    private  CshTransactionRefundService cshTransactionRefundService;

    @RequestMapping({"/csh/transaction/refund/query"})
    @ResponseBody
    public ResponseData detailQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, HlsCusCshTransactionRefund hlsCusCshTransactionRefund, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }

        IRequest requestCtx = createRequestContext(request);
        HlsCusCshTransactionRefund metadataRelation = param.toJavaObject(HlsCusCshTransactionRefund.class);
        if (hlsCusCshTransactionRefund.getRefundId() != null) {
            metadataRelation.setRefundId(hlsCusCshTransactionRefund.getRefundId());
        }
        return new ResponseData(cshTransactionRefundService.cshTransactionRefundQuery(metadataRelation, pagenum, pagesize,sortName,sortOrder));
    }

    @RequestMapping({"/csh/transaction/refundQuery/query"})
    @ResponseBody
    public ResponseData refunddetailQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response, HlsCusCshTransactionRefund hlsCusCshTransactionRefund, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        String sortName=null;
        String sortOrder=null;
        if(param.get("sort_name")!=null){
            sortName = param.get("sort_name").toString();
        }
        if(param.get("sort_name")!=null){
            sortOrder=param.get("sort_order").toString();
        }

        IRequest requestCtx = createRequestContext(request);
        HlsCusCshTransactionRefund metadataRelation = param.toJavaObject(HlsCusCshTransactionRefund.class);
        if(param.get("bp_name")!=null){
            metadataRelation.setBpName(param.get("bp_name").toString());
        }
        if(param.get("contract_number")!=null){
            metadataRelation.setContractNumber(param.get("contract_number").toString());
        }
        if (hlsCusCshTransactionRefund.getRefundId() != null) {
            metadataRelation.setRefundId(hlsCusCshTransactionRefund.getRefundId());
        }
        return new ResponseData(cshTransactionRefundService.cshPaymentTransactionRefundQuery(metadataRelation, pagenum, pagesize,sortName,sortOrder));
    }

    /**
     * 二期功能：退款申请创建tab页查询
     */
    @RequestMapping(value = "/csh/transaction/refund/create/query")
    @ResponseBody
    public ResponseData createRefundQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                          @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                          HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        requestContext.setAttribute("authorityRuleFlag", "N");
        JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
        HlsCusCshTransactionRefund dto = param.toJavaObject(HlsCusCshTransactionRefund.class);
        return new ResponseData(cshTransactionRefundService.createRefundQuery(requestContext, dto, pagenum, pagesize));
    }

    /**
     * 二期功能：退款申请维护tab页查询
     */
    @RequestMapping(value = "/csh/transaction/refund/modify/home/query")
    @ResponseBody
    public ResponseData modifyHomeQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                        HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        requestContext.setAttribute("authorityRuleFlag", "N");
        JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
        HlsCusCshTransactionRefund dto = param.toJavaObject(HlsCusCshTransactionRefund.class);
        return new ResponseData(cshTransactionRefundService.modifyHomeQuery(requestContext, dto, pagenum, pagesize));
    }

    /**
     * 二期功能：退款明细页面行信息查询
     */
    @RequestMapping(value = "/csh/transaction/refund/detail/query")
    @ResponseBody
    public ResponseData refundInfoLnQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        requestContext.setAttribute("authorityRuleFlag", "N");
        JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
        HlsCusCshTransactionRefund dto = param.toJavaObject(HlsCusCshTransactionRefund.class);
        return new ResponseData(cshTransactionRefundService.refundInfoLnQuery(requestContext, dto, pagenum, pagesize));
    }

    /**
     * 二期功能：待支付清单-退款申请 首页查询
     */
    @RequestMapping(value = "/csh/transaction/refund/pay/home")
    @ResponseBody
    public ResponseData refundPayHome(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                                      HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
        HlsCusCshTransactionRefund dto = param.toJavaObject(HlsCusCshTransactionRefund.class);
        return new ResponseData(cshTransactionRefundService.refundPayHome(requestContext, dto, pagenum, pagesize));
    }

    //收款退款申请
    @RequestMapping(value = "/csh/transaction/refund/payment/submit")
    @ResponseBody
    public ResponseData refundSubmit(HttpServletRequest request,
                                     @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpSession session) throws BeyondAmountLimitException, ResMessageException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusCshTransactionRefund> hlsCusCshTransactions = parameter.toJavaList(HlsCusCshTransactionRefund.class);
        IRequest requestCtx = createRequestContext(request);
        cshTransactionRefundService.refundCshTransactionSubmit(requestCtx, session, hlsCusCshTransactions);
        return new ResponseData();
    }

    @RequestMapping(value = "/csh/transaction/refund/save")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws ResMessageException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusCshTransactionRefund> list = param.toJavaList(HlsCusCshTransactionRefund.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(cshTransactionRefundService.saveLn(requestCtx, list));
    }

    /**
     * 二期功能：退款申请创建页面保存按钮逻辑
     * @param requestData
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/csh/transaction/refund/with-lines/save")
    @ResponseBody
    public ResponseData createAndUpdate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws Exception {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
        requestCtx.setAttribute("authorityRuleFlag", "N");
        CshBaseDto cshBaseDto = param.toJavaObject(CshBaseDto.class);
        return cshTransactionRefundService.createAndUpdate(requestCtx, cshBaseDto);
    }

    /**
     * 二期功能：退款申请创建/维护页面  提交按钮  逻辑
     * @param refundId
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/csh/transaction/refund/with-lines/submit")
    @ResponseBody
    public ResponseData refundSubmit(Long refundId, HttpServletRequest request) throws Exception {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        requestCtx.setAttribute("authorityRuleFlag", "N");
        return cshTransactionRefundService.refundSubmit(requestCtx, refundId);
    }

    /**
     * 二期功能：查询申请单的支付状态
     * @param requestData
     * @param request
     * @param response
     * @param hlsCusCshTransactionRefund
     * @param pagenum
     * @param pagesize
     * @return
     */
    @RequestMapping({"/csh/transaction/refund/status"})
    @ResponseBody
    public ResponseData queryStatus(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request,
                                    HttpServletResponse response, HlsCusCshTransactionRefund hlsCusCshTransactionRefund,
                                    @RequestParam(defaultValue = "1") int pagenum,
                                    @RequestParam(defaultValue = "10") int pagesize) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusCshTransactionRefund refund = (HlsCusCshTransactionRefund)param.toJavaObject(HlsCusCshTransactionRefund.class);
        List<HlsCusCshTransactionRefund> result = new ArrayList<>();
        result.add(cshTransactionRefundService.selectByPrimaryKey(requestContext, refund));
        return new ResponseData(result);
    }
}
