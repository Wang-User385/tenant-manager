//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;


import com.hand.hls.cont.dto.HlsCusHlsBpMasterBankAccount;
import com.hand.hls.cont.service.HlsBpMasterBankAccountService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HlsBpMasterBankAccountController extends BaseController {
    @Autowired
    private HlsBpMasterBankAccountService service;

    public HlsBpMasterBankAccountController() {
    }

    @RequestMapping({"/hls/bp/bankAccount/query"})
    @ResponseBody
    public ResponseData query(HlsCusBpMasterBankAccount bpMasterBankAccount, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.selectAll(requestContext, bpMasterBankAccount, page, pagesize));
    }

    @RequestMapping({"/hls/debt/account/query"})
    @ResponseBody
    public ResponseData debtAccountQuery(HlsCusBpMasterBankAccount bpMasterBankAccount, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        return new ResponseData(this.service.debtAccountQuery(requestContext, bpMasterBankAccount, page, pagesize));
    }


    @RequestMapping({"/hls/bp/bank/account/queryLov"})
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              HttpServletRequest request, HttpServletResponse response,
                              HlsCusHlsBpMasterBankAccount hlsCusHlsBpMasterBankAccount,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusHlsBpMasterBankAccount metadataRelation = param.toJavaObject(HlsCusHlsBpMasterBankAccount.class);
        IRequest requestCtx = createRequestContext(request);
        if (hlsCusHlsBpMasterBankAccount.getBpId() != null) {
            metadataRelation.setBpId(hlsCusHlsBpMasterBankAccount.getBpId());
        }
        List<HlsCusHlsBpMasterBankAccount> list = service.queryHlsBpMasterBankAccountLov(requestCtx, metadataRelation, pagenum, pagesize);
        return new ResponseData(list);
    }


    @RequestMapping({"/hls/bp/bank/account/submit"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusBpMasterBankAccount> list = param.toJavaList(HlsCusBpMasterBankAccount.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }


}
