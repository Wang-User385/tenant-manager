package com.hand.hls.partner.controllers;

import cfca.paperless.client.util.JsonUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.*;
import com.hand.hls.partner.service.YLInterfaceService;
import com.hand.hls.partner.util.RsaAesUtils;
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
    public JSONObject placeOrder(@RequestBody JSONObject jsonObject, HttpServletRequest request) {

        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.placeOrder(jsonObject, request, iRequest);
        }catch (Exception e){
            //捕获异常
            ResponseData responseData = new ResponseData();
            responseData.setCode("400");
            responseData.setMessage("风控预审拒绝");
            try {
                jsonObject1 = RsaAesUtils.encryptedData(JSONObject.toJSONString(responseData));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/closeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject closeOrder(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.closeOrder(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/queryOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject queryOrder(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.queryOrder(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/repayment"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject repayment(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.repayment(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/compensatoryTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject compensatoryTrialCalculation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.compensatoryTrialCalculation(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/claimsSubrogation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject claimsSubrogation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.claimsSubrogation(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/advancesSettleTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject advancesSettleTrialCalculation(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.advancesSettleTrialCalculation(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/advancesSettleRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject advancesSettleRequest(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.advancesSettleRequest(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/dataAcquisition"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject dataAcquisition(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.dataAcquisition(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/overdueRepurchaseTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject overdueRepurchaseTrialCalculation(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.overdueRepurchaseTrialCalculation(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/overdueRepurchaseRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject overdueRepurchaseRequest(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.overdueRepurchaseRequest(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/queryWithholdingState"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject queryWithholdingState(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.queryWithholdingState(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/stopWithholding"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject stopWithholding(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.stopWithholding(jsonObject,iRequest,request);
    }

    @RequestMapping(
            value = {"/di/recoverWithholding"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject recoverWithholding(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return ylInterfaceService.recoverWithholding(jsonObject,iRequest, request);
    }
}
