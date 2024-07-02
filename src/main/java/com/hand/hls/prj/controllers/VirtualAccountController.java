package com.hand.hls.prj.controllers;

import com.hand.hls.exception.HlsCusException;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.prj.dto.VirtualAccount;
import com.hand.hls.prj.service.IVirtualAccountService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.hand.hap.core.impl.RequestHelper;
import leaf.bean.LeafRequestData;
import org.springframework.web.bind.annotation.*;

@Controller
public class VirtualAccountController extends BaseController {

    @Autowired
    private IVirtualAccountService service;


    @RequestMapping(value = "/hls/virtual/account/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        JSONObject param = (JSONObject) requestData.get("parameter");
        VirtualAccount dto = param.toJavaObject(VirtualAccount.class);
        return new ResponseData(service.select(requestContext, dto, pagenum, pagesize));
    }

    @RequestMapping(value = "/hls/virtual/account/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<VirtualAccount> list = param.toJavaList(VirtualAccount.class);
        getValidator().validate(list, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(service.batchUpdate(requestCtx, list));
    }

    @RequestMapping(value = "/hls/virtual/account/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<VirtualAccount> dto = parameter.toJavaList(VirtualAccount.class);
        service.batchDelete(dto);
        return new ResponseData(dto);
    }

    @RequestMapping("/hls/virtual/batch/excel/import")
    public ResponseData prjBatchExcelImport(HttpServletRequest request, Long headerId) throws HlsCusException {
        ResponseData responseData = new ResponseData();
        IRequest iRequest = createRequestContext(request);
        service.batchImportVirtualAccount(iRequest, headerId);
        return responseData;
    }

    /**
     * 批量删除虚拟账户
     * @param request 请求信息
     * @param requestData 请求数据
     * @return 成功消息
     */
    @RequestMapping(value = "/hls/virtual/account/delete")
    @ResponseBody
    public ResponseData virtualAccountDelete(HttpServletRequest request,
                             @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<VirtualAccount> virtualAccounts = parameter.toJavaList(VirtualAccount.class);
        IRequest requestCtx = createRequestContext(request);
        requestCtx.setAttribute("authorityRuleFlag", "N");
        service.batchDeleteVirtualAccount (requestCtx ,virtualAccounts);
        return new ResponseData(true);
    }

    /**
     * 保存前数据重复校验
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/virtual/account/check/before/submit")
    @ResponseBody
    public ResponseData hlsVirtualAccountCheckBeforeSubmit(HttpServletRequest request, @RequestParam HashMap params) {
        IRequest iRequest = createRequestContext(request);
        String code = service.virtualAccountCheckBeforeSubmit(iRequest,params);
        return new ResponseData(Arrays.asList(code));
    }

    /**
     * 保存后更新合同版本号
     * @param request
     * @return
     */
    @RequestMapping(value = "/hls/virtual/account/contract/update")
    @ResponseBody
    public ResponseData hlsVirtualAccountContractUpdate(HttpServletRequest request, @RequestParam HashMap params) {
        IRequest iRequest = createRequestContext(request);
        service.virtualAccountContractUpdate(iRequest,params);
        return new ResponseData(true);
    }
}