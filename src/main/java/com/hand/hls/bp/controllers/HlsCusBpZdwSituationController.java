package com.hand.hls.bp.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpZdwSituation;
import com.hand.hls.bp.service.HlsCusIBpZdwSituationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
public class HlsCusBpZdwSituationController extends BaseController {

    @Autowired
    private HlsCusIBpZdwSituationService service;


    @RequestMapping(value = "/hls/bp/zdw/situation/query")
    @ResponseBody
    public ResponseData query(HlsCusBpZdwSituation dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAll(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/hls/bp/zdw/situation/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<HlsCusBpZdwSituation> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        Date date = new Date();
        IRequest requestCtx = createRequestContext(request);
        if(CollectionUtils.isNotEmpty(dto)){
            for (int i=0;i<dto.size();i++) {
                dto.get(i).setZdwSituationUpdateTime(date);
            }
        }
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/hls/bp/zdw/situation/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusBpZdwSituation> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}