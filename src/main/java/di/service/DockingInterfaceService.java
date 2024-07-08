package di.service;

import com.hand.hap.system.dto.ResponseData;
import di.dto.*;

import javax.servlet.http.HttpServletRequest;

public interface DockingInterfaceService {
    ResponseData placeOrder(PlaceOrderDTO placeOrderDTO, HttpServletRequest request);

    ResponseData closeOrder(CloseOrderDTO closeOrderDTO, HttpServletRequest request);

    ResponseData queryOrder(String orderNo, HttpServletRequest request);

    ResponseData repayment(RepayMent repayMent, HttpServletRequest request);

    ResponseData compensatoryTrialCalculation(CompensatoryTrialCalculationDTO compensatoryTrialCalculation, HttpServletRequest request);

    ResponseData claimsSubrogation(ClaimsSubrogationDTO claimsSubrogationDTO, HttpServletRequest request);
}
