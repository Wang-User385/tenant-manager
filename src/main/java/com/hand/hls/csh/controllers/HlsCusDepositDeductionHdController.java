package com.hand.hls.csh.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.HlsCusDepositDeductionHd;
import com.hand.hls.csh.service.HlsCusDepositDeductionHdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCusDepositDeductionHdController extends BaseController {

    @Autowired
    private HlsCusDepositDeductionHdService service;


    @RequestMapping(value = "/csh/deposit/deduction/hd/query")
    @ResponseBody
    public ResponseData query(HlsCusDepositDeductionHd dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectHlsCusDepositHeaderData(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/csh/deposit/deduction/hd/queryById")
    @ResponseBody
    public ResponseData queryById(HlsCusDepositDeductionHd dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        HlsCusDepositDeductionHd deductionHd=service.selectHlsCusDepositDeductionData(requestContext,dto.getDepositDeductionHdId());
        List<HlsCusDepositDeductionHd>  deductionHdList=new ArrayList<>();
        deductionHdList.add(deductionHd);
        return new ResponseData(deductionHdList);
    }

    @RequestMapping(value = "/csh/deposit/deduction/hd/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusDepositDeductionHd> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/csh/deposit/deduction/hd/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody HlsCusDepositDeductionHd depositDeductionHd) {
        IRequest requestCtx = createRequestContext(request);
        service.deleteDepositDeductionHd(requestCtx,depositDeductionHd);
        return new ResponseData();
    }
}