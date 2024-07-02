package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.dto.HlsCusDepositDeduction;
import com.hand.hls.csh.dto.HlsCusDepositRefund;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.service.HlsCusDepositDeductionService;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCusDepositDeductionController extends BaseController {

    @Autowired
    private HlsCusDepositDeductionService service;


    @RequestMapping(value = "/csh/deposit/deduction/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusDepositDeduction dto = param.toJavaObject(HlsCusDepositDeduction.class);

        return new ResponseData(service.selectDepositDedctionData(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/csh/deposit/deduction/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusDepositDeduction> dto, BindingResult result, HttpServletRequest request, HttpSession session)throws BeyondAmountLimitException, IllegalArgumentException,HlsCusException {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.saveDepositDedctionData(requestCtx,dto));
    }

    @RequestMapping(value = "/csh/deposit/deduction/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusDepositDeduction> dto) {
        service.batchDelete(dto);
        return new ResponseData(new ArrayList<>(dto));
    }

    @RequestMapping(value = "/csh/deposit/deduction/queryNewAmout")
    @ResponseBody
    public Double queryNewAmout(HlsCusDepositDeduction dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.selectNewDectionAmountSum(dto);
    }


    @RequestMapping(value = "/csh/deposit/deduction/approval")
    @ResponseBody
    public ResponseData approval(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request, HttpSession session)throws BeyondAmountLimitException, IllegalArgumentException,HlsCusException {
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusDepositDeduction> dto = parameter.toJavaList(HlsCusDepositDeduction.class);

        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        service.approvalDepositDeduction(requestCtx,dto);
        return new ResponseData();
    }
}