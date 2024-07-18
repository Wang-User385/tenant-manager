package com.hand.hls.partner.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.*;


import javax.servlet.http.HttpServletRequest;

public interface YLInterfaceService {
    ResponseData placeOrder(JSONObject jsonObject, HttpServletRequest request, IRequest iRequest);

    ResponseData closeOrder(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData queryOrder(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData repayment(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData compensatoryTrialCalculation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData claimsSubrogation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData advancesSettleTrialCalculation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData advancesSettleRequest(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData dataAcquisition(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData overdueRepurchaseTrialCalculation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData overdueRepurchaseRequest(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData queryWithholdingState(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData stopWithholding(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);

    ResponseData recoverWithholding(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request);
}
