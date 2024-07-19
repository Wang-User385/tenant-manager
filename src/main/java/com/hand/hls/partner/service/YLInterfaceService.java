package com.hand.hls.partner.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.*;


import javax.servlet.http.HttpServletRequest;

public interface YLInterfaceService {
    JSONObject placeOrder(JSONObject jsonObject, HttpServletRequest request, IRequest iRequest);

    JSONObject closeOrder(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject queryOrder(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject repayment(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject compensatoryTrialCalculation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject claimsSubrogation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject advancesSettleTrialCalculation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject advancesSettleRequest(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject dataAcquisition(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject overdueRepurchaseTrialCalculation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject overdueRepurchaseRequest(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject queryWithholdingState(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject stopWithholding(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    JSONObject recoverWithholding(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);
}
