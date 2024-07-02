package com.hand.hls.bp.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpHistoricalEvolution;
import com.hand.hls.bp.service.HlsCusIBpHistoricalEvolutionService;
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
public class HlsCusBpHistoricalEvolutionController extends BaseController {

    @Autowired
    private HlsCusIBpHistoricalEvolutionService service;


    @RequestMapping(value = "/hls/bp/historical/evolution/query")
    @ResponseBody
    public ResponseData query(HlsCusBpHistoricalEvolution dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAll(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/hls/bp/historical/evolution/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusBpHistoricalEvolution> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hls/bp/historical/evolution/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusBpHistoricalEvolution> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}