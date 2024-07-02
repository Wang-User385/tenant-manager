package com.hand.hls.abs.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsProductOrganization;
import com.hand.hls.abs.service.HlsCusAbsProductOrganizationService;
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
public class HlsCusAbsProductOrganizationController extends BaseController {

    @Autowired
    private HlsCusAbsProductOrganizationService service;


    /**
     * 产品-机构查询
     * 产品明细-机构表格
     */
    @RequestMapping(value = "/ct/abs/product/organization/query")
    @ResponseBody
    public ResponseData query(HlsCusAbsProductOrganization dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryDetail(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/ct/abs/product/organization/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusAbsProductOrganization> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/ct/abs/product/organization/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAbsProductOrganization> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}
