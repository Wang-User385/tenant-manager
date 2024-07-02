package com.hand.hls.plm.pli.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.pli.dto.PlmPliFrequencySet;
import com.hand.hls.plm.pli.service.PlmPliFrequencySetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PostloanInspectionFrequencySetController extends BaseController {
    @Autowired
    private PlmPliFrequencySetService service;

    @RequestMapping(value = "/plm/pli/frequency/set/query")
    @ResponseBody
    public ResponseData query(PlmPliFrequencySet dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/pli/frequency/set/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<PlmPliFrequencySet> dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.batchUpdate2(requestContext, dto);
    }

    @RequestMapping(value = "/plm/pli/frequency/set/remove")
    @ResponseBody
    public ResponseData delete(@RequestBody List<PlmPliFrequencySet> dto, HttpServletRequest request) {
        service.batchDelete(dto);
        return new ResponseData();
    }
}
