package com.hand.hls.plm.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.plm.dto.PlmRiskFeedback;
import com.hand.hls.plm.service.PlmRiskFeedbackService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PlmRiskFeedbackController extends BaseController {

    @Autowired
    private PlmRiskFeedbackService service;

    /**
     * 附件查询 风险反馈信息
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/plm/risk/feedback/attachment/query/info")
    @ResponseBody
    public ResponseData plmRiskFeedbackQuery(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        PlmRiskFeedback dto = param.toJavaObject(PlmRiskFeedback.class);
        List<PlmRiskFeedback> list = service.plmRiskFeedbackQuery(requestContext, dto, page, pageSize);
        return new ResponseData(list);
    }

    @RequestMapping(value = "/plm/risk/feedback/attachment/submit")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) {
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<PlmRiskFeedback> dto = param.toJavaList(PlmRiskFeedback.class);
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.attachmentBatchUpdate(requestCtx, dto));
    }
}
