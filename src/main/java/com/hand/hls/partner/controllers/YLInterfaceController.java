package com.hand.hls.partner.controllers;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.*;
import com.hand.hls.partner.service.YLInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = {"/r/api"})
public class YLInterfaceController extends BaseController {
    
    @Autowired
    private YLInterfaceService ylInterfaceService;


    @RequestMapping(
            value = {"/di/placeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData placeOrder(@RequestBody PlaceOrderDTO placeOrderDTO, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.placeOrder(placeOrderDTO,request,iRequest);
    }

    @RequestMapping(
            value = {"/di/closeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData closeOrder(@RequestBody CloseOrderDTO closeOrderDTO, HttpServletRequest request) {
        return ylInterfaceService.closeOrder(closeOrderDTO,request);
    }

    @RequestMapping(
            value = {"/di/queryOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData queryOrder(@RequestParam String orderNo, HttpServletRequest request) {
        return ylInterfaceService.queryOrder(orderNo,request);
    }

    @RequestMapping(
            value = {"/di/repayment"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData repayment(@RequestBody RepayMent repayMent, HttpServletRequest request) {
        return ylInterfaceService.repayment(repayMent,request);
    }

    @RequestMapping(
            value = {"/di/compensatoryTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData compensatoryTrialCalculation(@RequestBody CompensatoryTrialCalculationDTO compensatoryTrialCalculation, HttpServletRequest request) {
        return ylInterfaceService.compensatoryTrialCalculation(compensatoryTrialCalculation,request);
    }

    @RequestMapping(
            value = {"/di/claimsSubrogation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData claimsSubrogation(@RequestBody ClaimsSubrogationDTO claimsSubrogationDTO, HttpServletRequest request) {
        return ylInterfaceService.claimsSubrogation(claimsSubrogationDTO,request);
    }

    @RequestMapping(
            value = {"/di/advancesSettleTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData advancesSettleTrialCalculation(AdvancesSettleComputeDTO advancesSettleComputeDTO,HttpServletRequest request) {
        return ylInterfaceService.advancesSettleTrialCalculation(advancesSettleComputeDTO,request);
    }

    @RequestMapping(
            value = {"/di/advancesSettleRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData advancesSettleRequest(AdvancesSettleComputeDTO advancesSettleComputeDTO,HttpServletRequest request) {
        return ylInterfaceService.advancesSettleRequest(advancesSettleComputeDTO,request);
    }
}
