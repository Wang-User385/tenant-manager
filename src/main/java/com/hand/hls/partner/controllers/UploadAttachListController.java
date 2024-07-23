package com.hand.hls.partner.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.partner.util.RsaAesUtils;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.partner.service.IUploadAttachListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
    @RequestMapping(value = {"/r/api"})
    public class UploadAttachListController extends BaseController{

    @Autowired
    private IUploadAttachListService service;
    @Autowired
    private IHlsWsRequestsService logService;

    @RequestMapping(
                value = {"/di/getUploadUrl"},
                method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject getUploadUrl(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
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
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
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

}