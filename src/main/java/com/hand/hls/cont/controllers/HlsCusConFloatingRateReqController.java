package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.service.HlsCusConFloatingRateReqService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class HlsCusConFloatingRateReqController extends BaseController {

    @Autowired
    private HlsCusConFloatingRateReqService service;

//    @RequestMapping(value = "/hlsCon/floating/rate/req/queryHeadList")
//    @ResponseBody
//    public ResponseData queryHeadList(HlsCusConFloatingRateReq dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request, HttpSession session) {
//        IRequest requestContext = createRequestContext(request);
//        Long companyId = (Long) session.getAttribute("companyId");
//        dto.setCompanyId(companyId);
//        return new ResponseData(service.queryHeadList(requestContext,dto,page,pageSize));
//    }

    /**
     * 启动调息工作流
     *
     * @param request
     * @param
     * @return
     */
    @RequestMapping(value = "/hlsCon/floating/rate/req/workflow/rateChange")
    @ResponseBody
    public ResponseData ctRateChangeWorkFlowStart(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        JSONObject param = (JSONObject) requestData.get("parameter");
        if (param == null) {
            return new ResponseData(false, "请求参数缺失!");
        }
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        HlsCusConFloatingRateReq dto = param.toJavaObject(HlsCusConFloatingRateReq.class);
        service.floatingRateWorkFlowStart(iRequest, dto);
        return new ResponseData();
    }
}