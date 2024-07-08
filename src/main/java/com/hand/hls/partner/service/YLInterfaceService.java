package com.hand.hls.partner.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.*;


import javax.servlet.http.HttpServletRequest;

public interface YLInterfaceService {
    ResponseData placeOrder(PlaceOrderDTO placeOrderDTO, HttpServletRequest request, IRequest iRequest);

    ResponseData closeOrder(CloseOrderDTO closeOrderDTO, HttpServletRequest request);

    ResponseData queryOrder(String orderNo, HttpServletRequest request);

    ResponseData repayment(RepayMent repayMent, HttpServletRequest request);

    ResponseData compensatoryTrialCalculation(CompensatoryTrialCalculationDTO compensatoryTrialCalculation, HttpServletRequest request);

    ResponseData claimsSubrogation(ClaimsSubrogationDTO claimsSubrogationDTO, HttpServletRequest request);

    ResponseData advancesSettleTrialCalculation(AdvancesSettleComputeDTO advancesSettleComputeDTO, HttpServletRequest request);

    ResponseData advancesSettleRequest(AdvancesSettleComputeDTO advancesSettleComputeDTO, HttpServletRequest request);
}
