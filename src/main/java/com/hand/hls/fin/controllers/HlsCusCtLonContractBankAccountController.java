package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fin.dto.HlsCusCtLonContractBankAccount;
import com.hand.hls.fin.service.HlsCusCtLonContractBankAccountService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusCtLonContractBankAccountController extends BaseController {

    @Autowired
    private HlsCusCtLonContractBankAccountService service;

    /**
     * 查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/hlsLon/contract/bankAccount/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCtLonContractBankAccount dto = param.toJavaObject(HlsCusCtLonContractBankAccount.class);
        return new ResponseData(service.selectConBankAccount(requestContext, dto, page, pageSize));
    }


//
//    /**
//     * 保存
//     * @param dto
//     * @param result
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/hlsLon/contract/bankAccount/submit")
//    @ResponseBody
//    public ResponseData update(@RequestBody List<HlsCusCtLonContractBankAccount> dto, BindingResult result, HttpServletRequest request) {
//        getValidator().validate(dto, result);
//        if (result.hasErrors()) {
//            ResponseData responseData = new ResponseData(false);
//            responseData.setMessage(getErrorMessage(result, request));
//            return responseData;
//        }
//        IRequest requestCtx = createRequestContext(request);
//        return new ResponseData(service.batchUpdate(requestCtx, dto));
//    }
//
//    /**
//     * 删除
//     * @param request
//     * @param dto
//     * @return
//     */
//    @RequestMapping(value = "/hlsLon/contract/bankAccount/remove")
//    @ResponseBody
//    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusCtLonContractBankAccount> dto) {
//        service.batchDelete(dto);
//        return new ResponseData();
//    }
//
//
//    /**
//     * 删除
//     * @param request
//     * @param bankAccount
//     * @return
//     */
//    @RequestMapping(value = "/hlsLon/contract/bankAccount/queryTypeCount")
//    @ResponseBody
//    public int queryTypeCount(HttpServletRequest request, HlsCusCtLonContractBankAccount bankAccount) {
//        return service.selectBankAccountTypeCount(bankAccount);
//
//    }
}