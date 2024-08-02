package com.hand.hls.partner.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.*;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;


import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface YLInterfaceService {
    String placeOrder(String decryptedStr,IRequest iRequest) throws HlsCusException;

    String closeOrder(String decryptedStr) throws HlsCusException;

    String queryOrder(String decryptedStr) throws HlsCusException;

    String repayment(String decryptedStr) throws HlsCusException;

    String compensatoryTrialCalculation(String decryptedStr) throws HlsCusException;

    String claimsSubrogation(String decryptedStr) throws HlsCusException;

    String advancesSettleTrialCalculation(String decryptedStr) throws HlsCusException;

    String advancesSettleRequest(String decryptedStr) throws HlsCusException;

    String dataAcquisition(String decryptedStr,IRequest iRequest) throws Exception;

    String overdueRepurchaseTrialCalculation(String decryptedStr) throws HlsCusException;

    String overdueRepurchaseRequest(String decryptedStr, IRequest iRequest) throws HlsCusException;

    String queryWithholdingState(String decryptedStr) throws HlsCusException;

    String stopWithholding(String decryptedStr) throws HlsCusException;

    String recoverWithholding(String decryptedStr) throws HlsCusException;

    String businessApplication(String decryptedStr, HttpServletRequest request, IRequest iRequest) throws HlsCusException, ResMessageException;

    String imageSync(String decryptedStr) throws HlsCusException;
}
