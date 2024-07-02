package com.hand.hls.inv.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.inv.dto.HlsCusFinancePurchaseDetailIdBak;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseDetailIdBakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HlsCusFinancePurchaseDetailIdBakController extends BaseController {

    @Autowired
    private HlsCusIFinancePurchaseDetailIdBakService service;


    @RequestMapping(value = "/inv/finance/purchase/detail/id/bak/query")
    @ResponseBody
    public ResponseData query(HlsCusFinancePurchaseDetailIdBak dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/inv/finance/purchase/detail/id/bak/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusFinancePurchaseDetailIdBak> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/inv/finance/purchase/detail/id/bak/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusFinancePurchaseDetailIdBak> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}