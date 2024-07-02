package com.hand.hls.inv.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.inv.dto.HlsCusFinancePurchaseBak;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseBakService;
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
public class HlsCusFinancePurchaseBakController extends BaseController {

    @Autowired
    private HlsCusIFinancePurchaseBakService service;


    @RequestMapping(value = "/inv/finance/purchase/bak/query")
    @ResponseBody
    public ResponseData query(HlsCusFinancePurchaseBak dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryAll(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/inv/finance/purchase/bak/submit")
    @ResponseBody
    public ResponseData update(@RequestBody HlsCusFinancePurchaseBak dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusFinancePurchaseBak> list = new ArrayList<>();
        list.add(service.submitBak(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/inv/finance/purchase/bak/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusFinancePurchaseBak> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * @Description:申购追加变更工作流
     * @Author: Wty
     * @Date: Created om 1:36 2018/5/2
     */
    @RequestMapping(value = "/inv/finance/purchase/bak/change/submit/wfl")
    @ResponseBody
    public ResponseData purchaseChangeSubmitWfl(HttpServletRequest request, @RequestBody HlsCusFinancePurchaseBak dto) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.submitWfl(iRequest, dto));
    }

}