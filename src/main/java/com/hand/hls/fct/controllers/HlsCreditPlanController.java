package com.hand.hls.fct.controllers;


import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;

import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.prj.dto.HlsCreditPlan;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.service.HlsCreditPlanService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCreditPlanController extends BaseController {

    @Autowired
    private HlsCreditPlanService service;

    //保理id补偿
    @RequestMapping({"/set/chanceId/plan"})
    @ResponseBody
    public ResponseData setChanceIDQuotation(@RequestParam("credit_plan_id")Long creditPlanId,
                                             @RequestParam("source_document_id" ) Long sourceDocumentId,
                                             HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setCreditPlanId(creditPlanId);
        hlsCreditPlan = service.selectByPrimaryKey(requestContext,hlsCreditPlan);
        hlsCreditPlan.setSourceDocumentId(sourceDocumentId);
        service.updateByPrimaryKey(requestContext,hlsCreditPlan);
        return new ResponseData();
    }



}
