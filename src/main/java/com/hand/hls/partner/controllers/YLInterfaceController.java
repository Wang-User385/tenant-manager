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
import javax.validation.Valid;

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
    public ResponseData placeOrder(@RequestBody @Valid PlaceOrderDTO placeOrderDTO, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.placeOrder(placeOrderDTO,request,iRequest);
    }

    @RequestMapping(
            value = {"/di/closeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData closeOrder(@RequestBody @Valid CloseOrderDTO closeOrderDTO, HttpServletRequest request) {
        return ylInterfaceService.closeOrder(closeOrderDTO,request);
    }

    @RequestMapping(
            value = {"/di/queryOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData queryOrder(@RequestBody @Valid QueryOrderDTO queryOrderDTO, HttpServletRequest request) {
        return ylInterfaceService.queryOrder(queryOrderDTO,request);
    }

    @RequestMapping(
            value = {"/di/repayment"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData repayment(@RequestBody @Valid RepayMent repayMent, HttpServletRequest request) {
        return ylInterfaceService.repayment(repayMent,request);
    }

    @RequestMapping(
            value = {"/di/compensatoryTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData compensatoryTrialCalculation(@RequestBody @Valid CompensatoryTrialCalculationDTO compensatoryTrialCalculation, HttpServletRequest request) {
        return ylInterfaceService.compensatoryTrialCalculation(compensatoryTrialCalculation,request);
    }

    @RequestMapping(
            value = {"/di/claimsSubrogation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData claimsSubrogation(@RequestBody @Valid ClaimsSubrogationDTO claimsSubrogationDTO, HttpServletRequest request) {
        return ylInterfaceService.claimsSubrogation(claimsSubrogationDTO,request);
    }

    @RequestMapping(
            value = {"/di/advancesSettleTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData advancesSettleTrialCalculation(@RequestBody @Valid AdvancesSettleComputeDTO advancesSettleComputeDTO,HttpServletRequest request) {
        return ylInterfaceService.advancesSettleTrialCalculation(advancesSettleComputeDTO,request);
    }

    @RequestMapping(
            value = {"/di/advancesSettleRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData advancesSettleRequest(@RequestBody @Valid AdvancesSettleComputeDTO advancesSettleComputeDTO,HttpServletRequest request) {
        return ylInterfaceService.advancesSettleRequest(advancesSettleComputeDTO,request);
    }

    @RequestMapping(
            value = {"/di/dataAcquisition"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData dataAcquisition(@RequestBody @Valid DataAcquisitionDTO dataAcquisitionDTO,HttpServletRequest request) {
        return ylInterfaceService.dataAcquisition(dataAcquisitionDTO,request);
    }

    @RequestMapping(
            value = {"/di/overdueRepurchaseTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData overdueRepurchaseTrialCalculation(@RequestBody @Valid OverdueRepurchaseTrialCalculationDTO overdueRepurchaseTrialCalculationDTO,HttpServletRequest request) {
        return ylInterfaceService.overdueRepurchaseTrialCalculation(overdueRepurchaseTrialCalculationDTO,request);
    }

    @RequestMapping(
            value = {"/di/overdueRepurchaseRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData overdueRepurchaseRequest(@RequestBody @Valid OverdueRepurchaseRequestDTO overdueRepurchaseRequestDTO,HttpServletRequest request) {
        return ylInterfaceService.overdueRepurchaseRequest(overdueRepurchaseRequestDTO,request);
    }

    @RequestMapping(
            value = {"/di/queryWithholdingState"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData queryWithholdingState(@RequestBody @Valid QueryWithholdingStateDTO queryWithholdingStateDTO,HttpServletRequest request) {
        return ylInterfaceService.queryWithholdingState(queryWithholdingStateDTO,request);
    }

    @RequestMapping(
            value = {"/di/stopWithholding"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData stopWithholding(@RequestBody @Valid StopWithholdingDTO stopWithholdingDTO,HttpServletRequest request) {
        return ylInterfaceService.stopWithholding(stopWithholdingDTO,request);
    }

    @RequestMapping(
            value = {"/di/recoverWithholding"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData recoverWithholding(@RequestBody @Valid RecoverWithholdingDTO recoverWithholdingDTO,HttpServletRequest request) {
        return ylInterfaceService.recoverWithholding(recoverWithholdingDTO,request);
    }
}
