package com.hand.hls.plm.pli.controllers;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.pli.dto.PlmPliUpcoming;
import com.hand.hls.plm.pli.service.PlmPliUpcomingService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class PlmPliUpcomingController extends BaseController {

    @Autowired
    private PlmPliUpcomingService service;
    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "/plm/pli/upcoming/query")
    @ResponseBody
    public ResponseData query(PlmPliUpcoming dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }


    /**
     * 查询贷后检查清单
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/plm/pli/upcoming/list/query")
    @ResponseBody
    public ResponseData upcomingListQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param  =(JSONObject) requestData.get("parameter");
        PlmPliUpcoming dto = param.toJavaObject(PlmPliUpcoming.class);

        return new ResponseData(service.upcomingList(requestContext,dto, page, pageSize));
    }

    @RequestMapping(value = "/plm/pli/upcoming/submit")
    @ResponseBody
    public ResponseData update(@RequestBody List<PlmPliUpcoming> dto, BindingResult result, HttpServletRequest request) {
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/plm/pli/upcoming/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<PlmPliUpcoming> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/plm/pli/upcoming/list/export")
    public void beforeQueryXLS(HttpServletRequest request,
                               HttpServletResponse httpServletResponse, HttpSession session) throws Exception {
        service.upcomingListDownloadExcel(request, httpServletResponse);
       // return new ResponseData();
    }
}