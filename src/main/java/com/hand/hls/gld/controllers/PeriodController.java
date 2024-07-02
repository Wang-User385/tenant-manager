package com.hand.hls.gld.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.gld.dto.Period;
import com.hand.hls.gld.service.IPeriodService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class PeriodController extends BaseController {

    @Autowired
    private IPeriodService service;


    @RequestMapping(value = "/gld/period/details/query")
    @ResponseBody
    public ResponseData query(Period dto, @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                              HttpServletRequest request) {
        if (requestData != null) {
            JSONObject param = (JSONObject) requestData.getParameter();
            if (param != null) {
                dto = param.toJavaObject(Period.class);
            }
        }
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, pagenum, pageSize));
    }

    @RequestMapping(value = "/gld/period/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<Period> dto = parameter.toJavaList(Period.class);
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/gld/period/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<Period> dto) {

        List<Period> periods = new ArrayList<>();

        dto.forEach(child -> {
            Period period = new Period();
            period.setPeriodName(child.getPeriodName());
            periods.add(period);
        });

        service.batchDelete(periods);
        return new ResponseData();
    }

    @RequestMapping(value = "/gld/period/create")
    @ResponseBody
    public ResponseData create(HttpServletRequest request,
                               @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        JSONObject param =(JSONObject) requestData.getParameter();
        HashMap<String, String> params = (HashMap<String, String>) JSONObject.parseObject(param.toJSONString(), new TypeReference<Map<String, String>>(){});
        IRequest requestCtx = createRequestContext(request);
        service.periodCreate(requestCtx, params);
        return new ResponseData();
    }

    @RequestMapping(value = "/gld/period/query4lov")
    @ResponseBody
    public ResponseData query4lovPeriod(Period dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request, HttpSession session) {
        IRequest requestContext = createRequestContext(request);
        Long companyId = (Long) session.getAttribute("companyId");
        dto.setCompanyId(companyId);
        return new ResponseData(service.periodQuery4Lov(requestContext, dto, page, pageSize));

    }

    @RequestMapping(value = "/gld/period/name/lov")
    @ResponseBody
    public ResponseData periodNameLov(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int pageNum,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request, HttpSession session) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Period dto = param.toJavaObject(Period.class);
        return new ResponseData(service.periodNameLov(requestContext, dto, pageNum, 12));

    }
}