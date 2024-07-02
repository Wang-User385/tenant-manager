package com.hand.hls.csh.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.csh.dto.HlsCusDepositDeduction;
import com.hand.hls.csh.dto.HlsCusDepositRefund;
import com.hand.hls.csh.service.HlsCusDepositRefundService;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import hls.core.utils.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HlsCusDepositRefundController extends BaseController {

    @Autowired
    private HlsCusDepositRefundService service;


    @RequestMapping(value = "/csh/deposit/refund/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,@RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusDepositRefund dto = param.toJavaObject(HlsCusDepositRefund.class);

        return new ResponseData(service.selectDepositRefundData(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/csh/deposit/refund/submit")
    @ResponseBody
    public ResponseData update(@RequestBody HlsCusDepositRefund depositRefund, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusDepositRefund> refundList = new ArrayList<>();
        if (depositRefund.getDepositRefundId() != null && !new Long(0L).equals(depositRefund.getDepositRefundId())) {
            refundList.add(service.updateByPrimaryKeySelective(requestCtx, depositRefund));
        } else {
            refundList.add(service.insertSelective(requestCtx, depositRefund));
        }
        return new ResponseData(refundList);
    }

    @RequestMapping(value = "/csh/deposit/refund/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusDepositRefund> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }


    @RequestMapping(value = "/csh/deposit/refund/approval")
    @ResponseBody
    public ResponseData approval(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws HlsCusException {

        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusDepositRefund> depositRefundList = parameter.toJavaList(HlsCusDepositRefund.class);
        IRequest requestCtx = createRequestContext(request);
        service.approvalDepositRefund(requestCtx, depositRefundList.get(0));
        return new ResponseData();
    }
}