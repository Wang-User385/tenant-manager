package com.hand.hls.partner.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.partner.util.RsaAesUtils;
import com.hand.hls.sys.dto.FndEmployee;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.mapper.FndEmployeeMapper;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import hls.core.utils.exception.HlsCusException;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.partner.service.IUploadAttachListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
    @RequestMapping(value = {"/r/api"})
    public class UploadAttachListController extends BaseController{

    @Autowired
    private IUploadAttachListService service;
    @Autowired
    private IHlsWsRequestsService logService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;
    @Autowired
    private FndEmployeeMapper fndEmployeeMapper;

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
                value = {"/di/getUploadUrl"},
                method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject getUploadUrl(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        IRequest iRequest = this.createIRequest(request);
        //step1 存储加密请求报文日志
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        hlsWsRequests.setRequestWsdlUrl(request.getRequestURI());
        hlsWsRequests.setFunctionName("GT-YL-F001获取文件上传URL接口");
        hlsWsRequests.setRequestJsonEncrypt(JSONObject.toJSONString(jsonObject));
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,iRequest);
        //step2 解密请求报文，存储解密请求报文日志
        String decryptedStr = RsaAesUtils.decryptedData(jsonObject);
        hlsWsRequests.setRequestJson(decryptedStr);
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,iRequest);
        //step3 业务逻辑处理
        String resStr = null;
        String returnStatus = "S";
        try{
            resStr = service.getUploadUrl(decryptedStr);
        }catch (Exception e){
            e.printStackTrace();
            returnStatus = "E";
            JSONObject resJson = new JSONObject();
            resJson.put("success",false);
            resJson.put("message","系统错误！");
            resStr = JSONObject.toJSONString(resJson);
        }
        //step4 存储返回报文日志
        hlsWsRequests.setResponseJson(resStr);
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,iRequest);
        //step5 加密返回报文，存储加密返回报文日志
        JSONObject encryptedResJson = RsaAesUtils.encryptedData(resStr);
        hlsWsRequests.setResponseJsonEncrypt(JSONObject.toJSONString(encryptedResJson));
        hlsWsRequests.setReturnStatus(returnStatus);
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,iRequest);

        return encryptedResJson;
    }

    @RequestMapping(
            value = {"/di/upload"},
            method = {RequestMethod.PUT})
    @ResponseBody
    public JSONObject upload(@RequestParam("fileId") String fileId,@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        IRequest iRequest = this.createIRequest(request);
        //step1 构造请求报文，存储请求报文日志
        JSONObject reqJson = new JSONObject();
        reqJson.put("fileId",fileId);
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        hlsWsRequests.setRequestWsdlUrl(request.getRequestURI());
        hlsWsRequests.setFunctionName("GT-YL-F002上传文件");
        hlsWsRequests.setRequestJson(JSONObject.toJSONString(reqJson));
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,iRequest);
        //step2 业务逻辑处理
        String resStr = null;
        String returnStatus = "S";
        try{
            resStr = service.upload(fileId,file);
        }catch (Exception e){
            e.printStackTrace();
            returnStatus = "E";
            JSONObject resJson = new JSONObject();
            resJson.put("success",false);
            resJson.put("message","系统错误！");
            resStr = JSONObject.toJSONString(resJson);
        }
        //step3 存储返回报文日志
        hlsWsRequests.setResponseJson(resStr);
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,iRequest);
        //step4 加密返回报文，存储加密返回报文日志
        JSONObject encryptedResJson = RsaAesUtils.encryptedData(resStr);
        hlsWsRequests.setResponseJsonEncrypt(JSONObject.toJSONString(encryptedResJson));
        hlsWsRequests.setReturnStatus(returnStatus);
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,iRequest);

        return encryptedResJson;
    }

    private IRequest createIRequest(HttpServletRequest request) throws HlsCusException {
        Long userId = createRequestContext(request).getUserId();
        User sysUser = userMapper.selectByPrimaryKey(userId);
        if (ObjectUtils.isEmpty(sysUser)) {
            throw new HlsCusException("未找到该用户");
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