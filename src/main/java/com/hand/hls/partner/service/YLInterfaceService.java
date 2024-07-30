package com.hand.hls.partner.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.*;
import hls.core.utils.exception.HlsCusException;


import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface YLInterfaceService {
    String placeOrder(String decryptedStr,IRequest iRequest) throws HlsCusException;

    String closeOrder(String decryptedStr) throws HlsCusException;

    String queryOrder(String decryptedStr);

    String repayment(String decryptedStr);

    String compensatoryTrialCalculation(String decryptedStr);

    String claimsSubrogation(String decryptedStr) throws HlsCusException;

    String advancesSettleTrialCalculation(String decryptedStr) throws HlsCusException;

    String advancesSettleRequest(String decryptedStr) throws HlsCusException;

    String dataAcquisition(String decryptedStr,IRequest iRequest) throws Exception;

    String overdueRepurchaseTrialCalculation(String decryptedStr) throws HlsCusException;

    String overdueRepurchaseRequest(String decryptedStr, IRequest iRequest) throws HlsCusException;

    String queryWithholdingState(String decryptedStr);

    String stopWithholding(String decryptedStr);

    String recoverWithholding(String decryptedStr);

    String businessApplication(String decryptedStr,HttpServletRequest request);

    String imageSync(String decryptedStr);
}
