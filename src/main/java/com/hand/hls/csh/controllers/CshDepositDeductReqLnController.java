package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.csh.dto.CshDepositDeductReqLn;
import com.hand.hls.csh.service.ICshDepositDeductReqLnService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CshDepositDeductReqLnController extends BaseController {

    @Autowired
    private ICshDepositDeductReqLnService service;
    @Autowired
    private IConContractService conContractService;


    @RequestMapping(value = "/csh/deposit/deduct/req/ln/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        CshDepositDeductReqLn dto = param.toJavaObject(CshDepositDeductReqLn.class);
//        return new ResponseData(service.select(requestContext,dto,pagenum,pagesize));

        //角色为管理公司的用户，在指定功能移除权限，并将user_id做为参数传入mapper中
        if(conContractService.isCompanyManageRole(requestContext,"EXTERNAL_MANAGE_USER")){
            dto.setManageUserId(requestContext.getUserId());
            requestContext.setAttribute("authorityRuleFlag", "N");
        }
        return new ResponseData(service.selectCshDepositDeductReqLnList(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/csh/deposit/deduct/req/ln/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<CshDepositDeductReqLn> list = param.toJavaList(CshDepositDeductReqLn.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/csh/deposit/deduct/req/ln/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<CshDepositDeductReqLn> dto = parameter.toJavaList(CshDepositDeductReqLn.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }
}