package com.hand.hls.partner.controllers;

import cfca.paperless.client.util.JsonUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.partner.dto.*;
import com.hand.hls.partner.service.YLInterfaceService;
import com.hand.hls.partner.util.RsaAesUtils;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = {"/r/api"})
public class YLInterfaceController extends BaseController {
    
    @Autowired
    private YLInterfaceService ylInterfaceService;
    @Autowired
    private IHlsWsRequestsService logService;

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

    @RequestMapping(
            value = {"/di/imageSync"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject imageSync(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-B003影像件同步",jsonObject,request);
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject resJson = new JSONObject();
            resJson.put("success",false);
            resJson.put("message","请求报文预处理失败！");
            return updateLogs(hlsWsRequests,JSONObject.toJSONString(resJson),"E");
        }

        String resStr = null;
        String returnStatus = "S";
        try{
             resStr = ylInterfaceService.imageSync(hlsWsRequests.getRequestJson());
        }catch (Exception e){
            e.printStackTrace();
            returnStatus = "E";
            JSONObject resJson = new JSONObject();
            resJson.put("success",false);
            resJson.put("message","系统错误！");
            resStr = JSONObject.toJSONString(resJson);
        }

        return this.updateLogs(hlsWsRequests,resStr,returnStatus);
    }

    private HlsWsRequests insertLogs(String functionName,JSONObject jsonObject,HttpServletRequest request) throws Exception {
        //step1 存储加密请求报文日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        hlsWsRequests.setRequestWsdlUrl(request.getRequestURI());
        hlsWsRequests.setFunctionName(functionName);
        hlsWsRequests.setRequestJsonEncrypt(JSONObject.toJSONString(jsonObject));
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,RequestHelper.getCurrentRequest());
        //step2 解密请求报文，存储解密请求报文日志
        String decryptedStr = RsaAesUtils.decryptedData(jsonObject);
        hlsWsRequests.setRequestJson(decryptedStr);
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,RequestHelper.getCurrentRequest());

        return hlsWsRequests;
    }

    private JSONObject updateLogs(HlsWsRequests hlsWsRequests,String resStr,String returnStatus){
        //step1 存储返回报文日志
        hlsWsRequests.setResponseJson(resStr);
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,RequestHelper.getCurrentRequest());
        //step2 加密返回报文，存储加密返回报文日志
        JSONObject encryptedResJson = new JSONObject();
        try {
            encryptedResJson = RsaAesUtils.encryptedData(resStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        hlsWsRequests.setResponseJsonEncrypt(JSONObject.toJSONString(encryptedResJson));
        hlsWsRequests.setReturnStatus(returnStatus);
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,RequestHelper.getCurrentRequest());
        return encryptedResJson;
    }

}
