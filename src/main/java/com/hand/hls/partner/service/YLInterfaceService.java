package com.hand.hls.partner.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.*;


import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface YLInterfaceService {
    String placeOrder(String decryptedStr,IRequest iRequest);

    String closeOrder(String decryptedStr);

    String queryOrder(String decryptedStr);

    String repayment(String decryptedStr);

    String compensatoryTrialCalculation(String decryptedStr);

    String claimsSubrogation(String decryptedStr);

    String advancesSettleTrialCalculation(String decryptedStr);

    String advancesSettleRequest(String decryptedStr);

    String dataAcquisition(String decryptedStr,IRequest iRequest) throws Exception;

    String overdueRepurchaseTrialCalculation(String decryptedStr);

    String overdueRepurchaseRequest(String decryptedStr);

    String queryWithholdingState(String decryptedStr);

    String stopWithholding(String decryptedStr);

    String recoverWithholding(String decryptedStr);

    String businessApplication(String decryptedStr,HttpServletRequest request);

    String imageSync(String decryptedStr);
}
