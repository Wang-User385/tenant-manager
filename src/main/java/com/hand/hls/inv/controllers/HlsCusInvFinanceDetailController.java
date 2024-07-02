package com.hand.hls.inv.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.inv.dto.HlsCusFinancePurchase;
import com.hand.hls.inv.dto.HlsCusFinanceRedeem;
import com.hand.hls.inv.dto.HlsCusInvFinanceDetail;
import com.hand.hls.inv.service.HlsCusInvFinanceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCusInvFinanceDetailController extends BaseController {

    @Autowired
    private HlsCusInvFinanceDetailService service;


    @RequestMapping(value = "/inv/finance/detail/query")
    @ResponseBody
    public ResponseData query(HlsCusInvFinanceDetail dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/inv/finance/detail/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusInvFinanceDetail> dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/inv/finance/detail/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusInvFinanceDetail> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/inv/finance/done/detail/query")
    @ResponseBody
    public ResponseData queryInvDoneFinanceList(HlsCusInvFinanceDetail dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryInvDoneFinanceList(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/inv/finance/detail/submit/wfl")
    @ResponseBody
    public ResponseData invFinanceDetailSubmit(@RequestBody HlsCusFinancePurchase dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusFinancePurchase> list = new ArrayList<>();
        list.add(service.invFinanceDetailSubmit(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/inv/finance/redeem/detail/submit/wfl")
    @ResponseBody
    public ResponseData invFinanceDetailSubmit(@RequestBody HlsCusFinanceRedeem dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusFinanceRedeem> list = new ArrayList<>();
        list.add(service.invFinanceDetailSubmit(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/inv/finance/purchase/query/page3")
    @ResponseBody
    public ResponseData queryInvFinanceDetal(HlsCusInvFinanceDetail dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.queryInvFinanceDetal(requestCtx, dto, page, pageSize));
    }
}