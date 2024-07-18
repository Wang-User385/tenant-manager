package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.partner.util.RsaAesUtils;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.partner.dto.UploadAttachList;
import com.hand.hls.partner.service.IUploadAttachListService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class UploadAttachListServiceImpl extends BaseServiceImpl<UploadAttachList> implements IUploadAttachListService{

    @Autowired
    private IHlsWsRequestsService logService;

    @Override
    public JSONObject getUploadUrl(JSONObject jsonObject, HttpServletRequest request,IRequest iRequest) throws Exception {
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
        //step3 请求报文转dto、业务逻辑处理
        UploadAttachList uploadAttachList = JSONObject.parseObject(decryptedStr, UploadAttachList.class);
        String fileId = UUID.randomUUID().toString();
        String uploadUrl = "/r/api/di/upload?fileId=" + fileId;
        uploadAttachList.setFileId(fileId);
        uploadAttachList.setUploadUrl(uploadUrl);
        this.insertSelective(iRequest,uploadAttachList);
        //step4 构造返回报文
        JSONObject resJson = new JSONObject();
        resJson.put("fileId",fileId);
        resJson.put("uploadUrl",uploadUrl);
        String resStr = JSONObject.toJSONString(resJson);
        //step5 存储返回报文日志
        hlsWsRequests.setResponseJson(resStr);
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,iRequest);
        //step6 加密返回报文，存储加密返回报文日志
        JSONObject encryptedResJson = RsaAesUtils.encryptedData(resStr);
        hlsWsRequests.setResponseJsonEncrypt(JSONObject.toJSONString(encryptedResJson));
        hlsWsRequests.setReturnStatus("S");
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,iRequest);

        return encryptedResJson;
    }
}