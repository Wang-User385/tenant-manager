package com.hand.hls.partner.controllers;

import cfca.paperless.client.util.JsonUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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


//    @RequestMapping(
//            value = {"/di/placeOrder"},
//            method = {RequestMethod.GET, RequestMethod.POST}
//    )
//    @ResponseBody
//    public ResponseData placeOrder(@RequestBody @Valid PlaceOrderDTO placeOrderDTO, HttpServletRequest request) {
//        IRequest iRequest = createRequestContext(request);
//        System.out.println(JSONObject.toJSONString(placeOrderDTO));
//        return null;
////        return ylInterfaceService.placeOrder(placeOrderDTO,request,iRequest);
//    }

    @RequestMapping(
            value = {"/di/placeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData placeOrder(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.placeOrder(jsonObject,request,iRequest);
    }

    @RequestMapping(
            value = {"/di/closeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData closeOrder(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.closeOrder(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/queryOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData queryOrder(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.queryOrder(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/repayment"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData repayment(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.repayment(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/compensatoryTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData compensatoryTrialCalculation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.compensatoryTrialCalculation(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/claimsSubrogation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData claimsSubrogation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.claimsSubrogation(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/advancesSettleTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData advancesSettleTrialCalculation(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.advancesSettleTrialCalculation(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/advancesSettleRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData advancesSettleRequest(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.advancesSettleRequest(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/dataAcquisition"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData dataAcquisition(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.dataAcquisition(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/overdueRepurchaseTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData overdueRepurchaseTrialCalculation(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.overdueRepurchaseTrialCalculation(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/overdueRepurchaseRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData overdueRepurchaseRequest(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.overdueRepurchaseRequest(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/queryWithholdingState"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData queryWithholdingState(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.queryWithholdingState(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/stopWithholding"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData stopWithholding(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.stopWithholding(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/recoverWithholding"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData recoverWithholding(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.recoverWithholding(jsonObject,iRequest, request);
    }
}
