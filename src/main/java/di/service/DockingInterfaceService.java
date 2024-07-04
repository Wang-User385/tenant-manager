package di.service;

import com.hand.hap.system.dto.ResponseData;
import di.dto.QueryOrder;
import di.dto.RepayMent;

import javax.servlet.http.HttpServletRequest;

public interface DockingInterfaceService {
    ResponseData placeOrder(String name, String idCardNo, String mobile, String productCode, String outBizNo, String idissue, String idexp, HttpServletRequest request);

    ResponseData closeOrder(String orderNo, String reason, HttpServletRequest request);

    ResponseData queryOrder(String orderNo, HttpServletRequest request);

    ResponseData repayment(RepayMent repayMent, HttpServletRequest request);
}
