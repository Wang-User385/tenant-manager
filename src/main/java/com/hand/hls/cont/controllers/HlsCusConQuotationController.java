package com.hand.hls.cont.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConQuotation;
import com.hand.hls.cont.service.HlsCusConQuotationService;
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
public class HlsCusConQuotationController extends BaseController {

    @Autowired
    private HlsCusConQuotationService service;


    @RequestMapping(value = "/ct/con/quotation/query")
    @ResponseBody
    public ResponseData query(HlsCusConQuotation dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/ct/con/quotation/submit")
    @ResponseBody
    public ResponseData update(@RequestBody HlsCusConQuotation dto, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusConQuotation> list = new ArrayList<>();
        list.add(service.conQuotationSave(requestCtx, dto));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/con/quotation/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusConQuotation> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}