package com.hand.hls.partner.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.partner.service.YLInterfaceService;
import com.hand.hls.partner.util.RsaAesUtils;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import hls.core.utils.exception.HlsCusException;
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
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-B001-下单",jsonObject,request);
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
            resStr = ylInterfaceService.placeOrder(hlsWsRequests.getRequestJson(),iRequest);
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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

    @RequestMapping(
            value = {"/di/closeOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject closeOrder(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-B005-关单",jsonObject,request);
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
            resStr = ylInterfaceService.closeOrder(hlsWsRequests.getRequestJson());
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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

    @RequestMapping(
            value = {"/di/queryOrder"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject queryOrder(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-A001-订单查询",jsonObject,request);
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
            resStr = ylInterfaceService.queryOrder(hlsWsRequests.getRequestJson());
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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

    @RequestMapping(
            value = {"/di/repayment"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject repayment(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-A002-还款",jsonObject,request);
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
            resStr = ylInterfaceService.repayment(hlsWsRequests.getRequestJson());
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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

    @RequestMapping(
            value = {"/di/compensatoryTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject compensatoryTrialCalculation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-A003-代偿试算",jsonObject,request);
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
            resStr = ylInterfaceService.compensatoryTrialCalculation(hlsWsRequests.getRequestJson());
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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

    @RequestMapping(
            value = {"/di/claimsSubrogation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject claimsSubrogation(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-A004-代偿请求",jsonObject,request);
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
            resStr = ylInterfaceService.claimsSubrogation(hlsWsRequests.getRequestJson());
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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

    @RequestMapping(
            value = {"/di/advancesSettleTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject advancesSettleTrialCalculation(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-A005-提前结清试算",jsonObject,request);
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
            resStr = ylInterfaceService.advancesSettleTrialCalculation(hlsWsRequests.getRequestJson());
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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

    @RequestMapping(
            value = {"/di/advancesSettleRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject advancesSettleRequest(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-A006-提前结清请求",jsonObject,request);
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
            resStr = ylInterfaceService.advancesSettleRequest(hlsWsRequests.getRequestJson());
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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

    @RequestMapping(
            value = {"/di/dataAcquisition"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject dataAcquisition(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-B002-数据采集",jsonObject,request);
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
            resStr = ylInterfaceService.dataAcquisition(hlsWsRequests.getRequestJson(),iRequest);
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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

    @RequestMapping(
            value = {"/di/overdueRepurchaseTrialCalculation"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject overdueRepurchaseTrialCalculation(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-A007-逾期回购试算",jsonObject,request);
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
            resStr = ylInterfaceService.overdueRepurchaseTrialCalculation(hlsWsRequests.getRequestJson());
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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

    @RequestMapping(
            value = {"/di/overdueRepurchaseRequest"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject overdueRepurchaseRequest(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-A008-逾期回购请求",jsonObject,request);
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
            resStr = ylInterfaceService.overdueRepurchaseRequest(hlsWsRequests.getRequestJson(),iRequest);
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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

    @RequestMapping(
            value = {"/di/queryWithholdingState"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject queryWithholdingState(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-A009-代扣状态查询",jsonObject,request);
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
            resStr = ylInterfaceService.queryWithholdingState(hlsWsRequests.getRequestJson());
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

    @RequestMapping(
            value = {"/di/stopWithholding"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject stopWithholding(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-A010-暂停代扣",jsonObject,request);
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
            resStr = ylInterfaceService.stopWithholding(hlsWsRequests.getRequestJson());
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

    @RequestMapping(
            value = {"/di/recoverWithholding"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject recoverWithholding(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-A011-恢复代扣",jsonObject,request);
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
            resStr = ylInterfaceService.recoverWithholding(hlsWsRequests.getRequestJson());
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

    @RequestMapping(
            value = {"/di/businessApplication"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject businessApplication(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-B004-业务申请",jsonObject,request);
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
            resStr = ylInterfaceService.businessApplication(hlsWsRequests.getRequestJson(),request,iRequest);
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
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
