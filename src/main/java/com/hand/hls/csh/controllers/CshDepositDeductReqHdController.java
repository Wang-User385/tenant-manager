package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.service.ICshDepositDeductReqHdService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.utils.HlsConstantUtil;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CshDepositDeductReqHdController extends BaseController {

    private static final String EXTERNAL_MANAGE_USER = "EXTERNAL_MANAGE_USER";

    @Autowired
    private ICshDepositDeductReqHdService service;
    @Autowired
    private IConContractService conContractService;

    @RequestMapping(value = "/csh/deposit/deduct/req/hd/init/query")
    @ResponseBody
    public ResponseData init_query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, CshDepositDeductReqHd cshDepositDeductReqHd, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);

        cshDepositDeductReqHd = service.selectCshDepositDeductReqHdInit(requestContext, cshDepositDeductReqHd);
        List<CshDepositDeductReqHd> cshDepositDeductReqHdList = new ArrayList<>();
        cshDepositDeductReqHdList.add(cshDepositDeductReqHd);

        return new ResponseData(cshDepositDeductReqHdList);
    }

    @RequestMapping(value = "/csh/deposit/deduct/req/hd/dtl/query")
    @ResponseBody
    public ResponseData dtl_query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, CshDepositDeductReqHd cshDepositDeductReqHd, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);

        cshDepositDeductReqHd = service.selectCshDepositDeductReqHd(requestContext, cshDepositDeductReqHd);
        List<CshDepositDeductReqHd> cshDepositDeductReqHdList = new ArrayList<>();
        cshDepositDeductReqHdList.add(cshDepositDeductReqHd);

        return new ResponseData(cshDepositDeductReqHdList);
    }

    @RequestMapping(value = "/csh/deposit/deduct/req/hd/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        CshDepositDeductReqHd dto = param.toJavaObject(CshDepositDeductReqHd.class);
//        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));

        //角色为管理公司的用户，在指定功能移除权限，并将user_id做为参数传入mapper中
        if(conContractService.isCompanyManageRole(requestContext,EXTERNAL_MANAGE_USER)){
            dto.setManageUserId(requestContext.getUserId());
            requestContext.setAttribute("authorityRuleFlag", "N");
        }
        return new ResponseData(service.selectCshDepositDeductReqHdList(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/csh/deposit/deduct/req/hd/save")
    @ResponseBody
    public ResponseData save(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        CshDepositDeductReqHd cshDepositDeductReqHd = param.toJavaObject(CshDepositDeductReqHd.class);
        getValidator().validate(cshDepositDeductReqHd, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }

        cshDepositDeductReqHd = service.saveDepositDeductReq(requestCtx, cshDepositDeductReqHd,"MANUAL");
        List<CshDepositDeductReqHd> cshDepositDeductReqHdList = new ArrayList<>();
        cshDepositDeductReqHdList.add(cshDepositDeductReqHd);

        return new ResponseData(cshDepositDeductReqHdList);
    }

    @RequestMapping(value = "/csh/deposit/deduct/req/hd/back")
    @ResponseBody
    public ResponseData back(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute("authorityRuleFlag","N");
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get(HlsConstantUtil.BaseController.PARAMETER);
        CshDepositDeductReqHd cshDepositDeductReqHd = param.toJavaObject(CshDepositDeductReqHd.class);
        return service.back(requestCtx, cshDepositDeductReqHd);
    }

    @RequestMapping(value = "/csh/deposit/deduct/req/hd/submit")
    @ResponseBody
    public ResponseData submitWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute("authorityRuleFlag","N");
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        CshDepositDeductReqHd cshDepositDeductReqHd = param.toJavaObject(CshDepositDeductReqHd.class);
        getValidator().validate(cshDepositDeductReqHd, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }

        cshDepositDeductReqHd = service.submitDepositDeductReq(requestCtx, cshDepositDeductReqHd);
        List<CshDepositDeductReqHd> cshDepositDeductReqHdList = new ArrayList<>();
        cshDepositDeductReqHdList.add(cshDepositDeductReqHd);

        return new ResponseData(cshDepositDeductReqHdList);
    }

    @RequestMapping(value = "/csh/deposit/deduct/req/hd/delete")
    @ResponseBody
    public ResponseData deleteDepositDeductReq(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        requestContext.setAttribute("authorityRuleFlag","N");
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        return new ResponseData(service.deleteDepositDeductReq(requestContext,param));
    }

}