package com.hand.hls.cont.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusContractTermination;
import com.hand.hls.cont.service.HlsCusContractTerminationService;
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
public class HlsCusContractTerminationController extends BaseController {

    @Autowired
    private HlsCusContractTerminationService service;


    @RequestMapping(value = "/con/contract/termination/query")
    @ResponseBody
    public ResponseData query(HlsCusContractTermination dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/con/contract/termination/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusContractTermination> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/con/contract/termination/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusContractTermination> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/con/contract/termination/contractId/query")
    @ResponseBody
    public ResponseData queryTerminationByContractId(Long contractId, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<HlsCusContractTermination> list = new ArrayList<>();
        list.add(service.queryTerminationByContractId(contractId));
        return new ResponseData(list);
    }

}