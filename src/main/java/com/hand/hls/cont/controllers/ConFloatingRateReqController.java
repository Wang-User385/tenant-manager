package com.hand.hls.cont.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.service.ConFloatingRateReqService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Qian Yuanfeng
 * @date 2020/6/16 - 14:13
 */
@Controller
public class ConFloatingRateReqController extends BaseController {
    private static final List<String> HLS_WFL_STATUS_SUBMIT = Arrays.asList("NEW", "REJECTED");
    @Autowired
    private ConFloatingRateReqService service;

    public ConFloatingRateReqController() {
    }

    @RequestMapping({"/con/floating/wfl/submit"})
    @ResponseBody
    public ResponseData submitFloatingRateWfl(HttpServletRequest request, HlsCusConFloatingRateReq rateReq) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        if (rateReq != null && rateReq.getFltReqId() != null) {
            rateReq = (HlsCusConFloatingRateReq)this.service.selectByPrimaryKey(iRequest, rateReq);
            return !HLS_WFL_STATUS_SUBMIT.contains(rateReq.getStatus()) ? new ResponseData(false, "本条调息正在审批中") : this.service.submitFloatingRateWfl(iRequest, rateReq);
        } else {
            return new ResponseData(false, "请求参数有误,请联系管理员。[fltReqId] is null");
        }
    }

    @RequestMapping(value = "/con/floating/query")
    @ResponseBody
    public ResponseData query(HlsCusConFloatingRateReq dto, @ModelAttribute("_request_data") LeafRequestData requestData, @RequestParam(defaultValue = "1") int pagenum, @RequestParam(defaultValue = "10") int pagesize, HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusConFloatingRateReq metadataRelation = (HlsCusConFloatingRateReq)param.toJavaObject(HlsCusConFloatingRateReq.class);
        return new ResponseData(this.service.queryConFloatingRateReq(requestContext, metadataRelation, pagenum, pagesize));
    }

    @RequestMapping({"/con/floating/wfl/submit/check"})
    @ResponseBody
    public ResponseData submitRateWflCheck(HttpServletRequest request, HlsCusConFloatingRateReq rateReq) {
        IRequest iRequest = this.createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        List<String> list = new ArrayList<>(1);
        list.add(service.confirmOldCashflow(iRequest,rateReq));
        return new ResponseData(list);

    }
}
