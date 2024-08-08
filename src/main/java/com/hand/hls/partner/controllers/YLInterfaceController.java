package com.hand.hls.partner.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.partner.dto.BusinessApplicationDTO;
import com.hand.hls.partner.service.YLInterfaceService;
import com.hand.hls.partner.util.RsaAesUtils;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.sys.dto.FndEmployee;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.mapper.FndEmployeeMapper;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import hls.core.utils.exception.HlsCusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = {"/r/api"})
public class YLInterfaceController extends BaseController {
    
    @Autowired
    private YLInterfaceService ylInterfaceService;
    @Autowired
    private IHlsWsRequestsService logService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;
    @Autowired
    private FndEmployeeMapper fndEmployeeMapper;
    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;

    private HttpServletRequest fakeRequest = new MockHttpServletRequest();
    private static String LOCAL = "zh_CN";
    private final IRequest setMdc(IRequest iRequest) {
        Map<String, String> mdcMap = MDC.getCopyOfContextMap();
        if (mdcMap != null) {
            mdcMap.forEach((k, v) -> iRequest.setAttribute(IRequest.MDC_PREFIX.concat(k), v));
        }
        return iRequest;
    }

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
    public JSONObject overdueRepurchaseRequest(@RequestBody JSONObject jsonObject,HttpServletRequest request, HttpSession session) {
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
            resStr = ylInterfaceService.overdueRepurchaseRequest(hlsWsRequests.getRequestJson(),iRequest ,session);
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
            value = {"/di/businessApplication"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public JSONObject businessApplication(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws HlsCusException {
        Long userId = createRequestContext(request).getUserId();
        User sysUser = userMapper.selectByPrimaryKey(userId);
        IRequest iRequest = this.createIRequest(sysUser);

        HlsWsRequests hlsWsRequests = null;
        try {
            hlsWsRequests = this.insertLogs("GT-YL-B004-业务申请",jsonObject,request);
        }catch(HlsCusException e){
            return updateLogs(hlsWsRequests,e.getMessage(),"S");
        }catch (Exception e) {
            e.printStackTrace();
            JSONObject resJson = new JSONObject();
            resJson.put("success",false);
            resJson.put("message","请求报文预处理失败！");
            return updateLogs(hlsWsRequests,JSONObject.toJSONString(resJson),"E");
        }
        BusinessApplicationDTO businessApplicationDTO = JSONObject.parseObject(hlsWsRequests.getRequestJson(), BusinessApplicationDTO.class);
        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(businessApplicationDTO.getOrderNo());
        sysUser = userMapper.queryByEmployeeId(hlsCusPrjProject.getEmployeeId());
        iRequest = this.createIRequest(sysUser);
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

    private IRequest createIRequest(User sysUser) throws HlsCusException {
        //Long userId = createRequestContext(request).getUserId();
        //User sysUser = userMapper.selectByPrimaryKey(userId);
        if (ObjectUtils.isEmpty(sysUser)) {
            throw new HlsCusException("未找到该项目的业务经理");
        }

        SysUserAllocation sysUserAllocation = new SysUserAllocation();
        sysUserAllocation.setUserId(sysUser.getUserId());
        List<SysUserAllocation> sysUserAllocationList = sysUserAllocationMapper.select(sysUserAllocation);
        if (sysUserAllocationList.size() <= 0) {
            throw new HlsCusException("用户" + sysUser.getUserName() + "没有进行用户分配!");
        }

        FndEmployee fndEmployee = fndEmployeeMapper.selectEmployeeInfoByAllocationId(sysUserAllocationList.get(0).getAllocationId().toString());
        if (ObjectUtils.isEmpty(fndEmployee)) {
            throw new HlsCusException("未找到员工(AllocationId：" + sysUserAllocationList.get(0).getAllocationId() + ")!");
        }

        HttpSession session = fakeRequest.getSession();

        session.setAttribute("userId", sysUser.getUserId());
        session.setAttribute("roleId", sysUserAllocationList.get(0).getRoleId());
        session.setAttribute("userName", sysUser.getUserName());
        session.setAttribute("companyId", sysUserAllocationList.get(0).getCompanyId());
        session.setAttribute("roleIds", null);
        session.setAttribute("allocationId", sysUserAllocationList.get(0).getAllocationId());
        session.setAttribute("employeeCode", fndEmployee.getEmployeeCode());

        IRequest iRequest = RequestHelper.createServiceRequest(fakeRequest);
        iRequest.setLocale(LOCAL);
        MDC.put("userId", sysUser.getUserId().toString());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        MDC.put("requestId", uuid);
        MDC.put("sessionId", "");
        iRequest = setMdc(iRequest);

        iRequest.setAttribute("userId", sysUser.getUserId());
        iRequest.setAttribute("roleId", sysUserAllocationList.get(0).getRoleId());
        iRequest.setAttribute("userName", sysUser.getUserName());
        iRequest.setAttribute("companyId", sysUserAllocationList.get(0).getCompanyId());
        iRequest.setAttribute("roleIds", null);
        iRequest.setAttribute("allocationId", sysUserAllocationList.get(0).getAllocationId());
        iRequest.setAttribute("employeeCode", fndEmployee.getEmployeeCode());

        RequestHelper.setCurrentRequest(iRequest);
        ServletWebRequest servletWebRequest = new ServletWebRequest(fakeRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        return iRequest;
    }

}
