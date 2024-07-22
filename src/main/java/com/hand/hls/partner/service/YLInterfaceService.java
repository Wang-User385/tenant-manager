package com.hand.hls.partner.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.*;


import javax.servlet.http.HttpServletRequest;

public interface YLInterfaceService {
    JSONObject placeOrder(JSONObject jsonObject, HttpServletRequest request, IRequest iRequest) throws Exception;

    JSONObject closeOrder(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject queryOrder(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject repayment(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject compensatoryTrialCalculation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject claimsSubrogation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject advancesSettleTrialCalculation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject advancesSettleRequest(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject dataAcquisition(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject overdueRepurchaseTrialCalculation(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject overdueRepurchaseRequest(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject queryWithholdingState(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject stopWithholding(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject recoverWithholding(JSONObject jsonObject,IRequest iRequest, HttpServletRequest request) throws Exception;

    JSONObject businessApplication(JSONObject jsonObject, IRequest iRequest, HttpServletRequest request) throws Exception;
}
