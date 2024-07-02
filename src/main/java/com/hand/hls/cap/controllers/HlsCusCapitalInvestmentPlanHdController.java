package com.hand.hls.cap.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cap.dto.HlsCusCapitalInvestmentPlanHd;
import com.hand.hls.cap.service.HlsCusCapitalInvestmentPlanHdService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCusCapitalInvestmentPlanHdController extends BaseController {
    private static final String PLAN_HEAD_ID = "plan_head_id";
    @Autowired
    private HlsCusCapitalInvestmentPlanHdService hlsCusCapitalInvestmentPlanHdService;

    @RequestMapping(value = "/cap/investment/plan/wfl/submit")
    @ResponseBody
    public ResponseData wlfSubmit(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject jsonObject = (JSONObject) requestData.get("parameter");
        Long planHeadId = Long.valueOf((Integer)jsonObject.get(PLAN_HEAD_ID));
        HlsCusCapitalInvestmentPlanHd hlsCusCapitalInvestmentPlanHd = new HlsCusCapitalInvestmentPlanHd();
        hlsCusCapitalInvestmentPlanHd.setPlanHeadId(planHeadId);
        hlsCusCapitalInvestmentPlanHd = hlsCusCapitalInvestmentPlanHdService.selectByPrimaryKey(requestCtx,hlsCusCapitalInvestmentPlanHd);
        List<HlsCusCapitalInvestmentPlanHd> dto = new ArrayList<>();
        dto.add(hlsCusCapitalInvestmentPlanHd);
        ResponseData responseData = new ResponseData();
        try {
            hlsCusCapitalInvestmentPlanHdService.capPlanSubmitWfl(requestCtx, dto);
            responseData.setSuccess(true);
        } catch (Exception e) {
            responseData.setSuccess(false);
            responseData.setMessage("工作流审批提交失败，请联系管理员！");
        }
        return responseData;
    }

}