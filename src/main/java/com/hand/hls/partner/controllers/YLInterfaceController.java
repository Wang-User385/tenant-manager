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
        } catch (Exception e) {
            e.printStackTrace();
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
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.closeOrder(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/queryOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject queryOrder(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.queryOrder(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/repayment"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject repayment(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.repayment(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/compensatoryTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject compensatoryTrialCalculation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.compensatoryTrialCalculation(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/claimsSubrogation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject claimsSubrogation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.claimsSubrogation(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/advancesSettleTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject advancesSettleTrialCalculation(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.advancesSettleTrialCalculation(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/advancesSettleRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject advancesSettleRequest(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.advancesSettleRequest(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/dataAcquisition"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject dataAcquisition(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.dataAcquisition(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/overdueRepurchaseTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject overdueRepurchaseTrialCalculation(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.overdueRepurchaseTrialCalculation(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/overdueRepurchaseRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject overdueRepurchaseRequest(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.overdueRepurchaseRequest(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/queryWithholdingState"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject queryWithholdingState(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.queryWithholdingState(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/stopWithholding"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject stopWithholding(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.stopWithholding(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/recoverWithholding"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject recoverWithholding(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.recoverWithholding(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    @RequestMapping(
            value = {"/di/businessApplication"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject businessApplication(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = ylInterfaceService.businessApplication(jsonObject, iRequest, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }
}
