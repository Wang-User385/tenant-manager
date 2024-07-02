//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.service.IAstFcEstimateResultService;
import javax.servlet.http.HttpServletRequest;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping({"/ast/fc/estimate/result"})
public class AstFcEstimateResultController extends BaseController {
    @Autowired
    private IAstFcEstimateResultService astFcEstimateResultService;

    public AstFcEstimateResultController() {
    }

    @RequestMapping({"/query"})
    @ResponseBody
    public ResponseData query(@ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return this.astFcEstimateResultService.queryByRequestData(requestContext, requestData, pagenum, pagesize);
    }

    @RequestMapping({"/submit"})
    public ResponseData submit(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return this.astFcEstimateResultService.batchUpdate(requestContext, requestData);
    }

    @RequestMapping({"/execute"})
    public ResponseData submit(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData, BindingResult result) throws BaseException {
        IRequest requestContext = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(requestContext);
        return this.astFcEstimateResultService.execute(requestContext, requestData);
    }

    //@PostMapping(value = "/assessment")
    @PostMapping({"/assessment"})
    public ResponseData assessment(final HttpServletRequest request,
                                   @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                   @RequestParam(value = "fcEstimateId") Long fcEstimateId,
                                   @RequestParam(value = "fiveClassPlan") String fiveClassPlan) {
        IRequest requestContext = createRequestContext(request);
        JSONObject para = (JSONObject) requestData.get("parameter");
        AstFcEstimate resources = para.toJavaObject((AstFcEstimate.class));

        AstFcEstimate astFcEstimate = new AstFcEstimate();
        astFcEstimate.setFcEstimateId(fcEstimateId);
        astFcEstimate.setFiveClassPlan(fiveClassPlan);
        ResponseData data = new ResponseData();

//            if (resources.get__status().equals("update")) {
//                resources.set__status("insert");
//            }

        astFcEstimateResultService.assessment(requestContext, astFcEstimate, resources);
        //functionService.updateFunctionResources(requestContext, function, resources);
        data.setSuccess(true);
        return data;
    }
}
