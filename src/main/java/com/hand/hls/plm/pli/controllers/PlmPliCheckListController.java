package com.hand.hls.plm.pli.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.pli.dto.PlmPliCheckList;
import com.hand.hls.plm.pli.service.PlmPliCheckListService;
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
public class PlmPliCheckListController extends BaseController {

    @Autowired
    private PlmPliCheckListService service;


    @RequestMapping(value = "/plm/pli/check/list/query")
    @ResponseBody
    public ResponseData query(PlmPliCheckList dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/pli/check/list/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<PlmPliCheckList> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/plm/pli/check/list/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<PlmPliCheckList> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}