package com.hand.hls.cap.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cap.dto.HlsCusCapFinancingPlan;
import com.hand.hls.cap.service.HlsCusCapFinancingPlanService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class HlsCusCapFinancingPlanController extends BaseController {

    @Autowired
    private HlsCusCapFinancingPlanService service;

    /**
     * 融资计划确认
     */
    @RequestMapping(value = "/cap/financing/plan/confirm")
    @ResponseBody
    public HlsCusCapFinancingPlan confirm(HttpServletRequest servletRequest, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        IRequest request = createRequestContext(servletRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusCapFinancingPlan plan = param.toJavaObject(HlsCusCapFinancingPlan.class);

        if (plan.getPlanId() != 0 && plan.getPlanId() != null) {
            plan.setConfirmStatus("FULL");
            service.updateByPrimaryKeySelective(request, plan);
        } else {
            throw new RuntimeException("请先保存！");
        }
        return plan;
    }
}
