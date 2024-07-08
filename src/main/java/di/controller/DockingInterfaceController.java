package di.controller;


//对接接口

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import di.dto.*;
import di.service.DockingInterfaceService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DockingInterfaceController extends BaseController {

    @Autowired
    private DockingInterfaceService dockingInterfaceService;


    @RequestMapping(
            value = {"/di/placeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData placeOrder(@RequestBody PlaceOrderDTO placeOrderDTO, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return dockingInterfaceService.placeOrder(placeOrderDTO,request,iRequest);
    }

    @RequestMapping(
            value = {"/di/closeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData closeOrder(@RequestBody CloseOrderDTO closeOrderDTO, HttpServletRequest request) {
        return dockingInterfaceService.closeOrder(closeOrderDTO,request);
    }

    @RequestMapping(
            value = {"/di/queryOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData queryOrder(@RequestParam String orderNo, HttpServletRequest request) {
        return dockingInterfaceService.queryOrder(orderNo,request);
    }

    @RequestMapping(
            value = {"/di/repayment"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData repayment(@RequestBody RepayMent repayMent, HttpServletRequest request) {
        return dockingInterfaceService.repayment(repayMent,request);
    }

    @RequestMapping(
            value = {"/di/compensatoryTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData compensatoryTrialCalculation(@RequestBody CompensatoryTrialCalculationDTO compensatoryTrialCalculation, HttpServletRequest request) {
        return dockingInterfaceService.compensatoryTrialCalculation(compensatoryTrialCalculation,request);
    }

    @RequestMapping(
            value = {"/di/claimsSubrogation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData claimsSubrogation(@RequestBody ClaimsSubrogationDTO claimsSubrogationDTO, HttpServletRequest request) {
        return dockingInterfaceService.claimsSubrogation(claimsSubrogationDTO,request);
    }

    @RequestMapping(
            value = {"/di/advancesSettleTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData advancesSettleTrialCalculation(AdvancesSettleComputeDTO advancesSettleComputeDTO,HttpServletRequest request) {
        return dockingInterfaceService.advancesSettleTrialCalculation(advancesSettleComputeDTO,request);
    }

    @RequestMapping(
            value = {"/di/advancesSettleRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData advancesSettleRequest(AdvancesSettleComputeDTO advancesSettleComputeDTO,HttpServletRequest request) {
        return dockingInterfaceService.advancesSettleRequest(advancesSettleComputeDTO,request);
    }
}
