package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.partner.dto.UploadAttachList;
import com.hand.hls.partner.service.IUploadAttachListService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class UploadAttachListServiceImpl extends BaseServiceImpl<UploadAttachList> implements IUploadAttachListService{

    @Autowired
    private IHlsWsRequestsService logService;

    @Override
    public ResponseData getUploadUrl(UploadAttachList uploadAttachList, HttpServletRequest request,IRequest iRequest){
        HlsWsRequests hlsWsRequests = new HlsWsRequests();
        hlsWsRequests.setRequestWsdlUrl(request.getRequestURI());
        hlsWsRequests.setFunctionName("GT-YL-F001获取文件上传URL接口");
        hlsWsRequests.setRequestJson(JSONObject.toJSONString(uploadAttachList));
        hlsWsRequests = logService.interfaceSave(hlsWsRequests,iRequest);

        String fileId = "abc";
        String uploadUrl = "http://2121";
        uploadAttachList.setFileId(fileId);
        uploadAttachList.setUploadUrl(uploadUrl);
        this.insertSelective(iRequest,uploadAttachList);

        List<UploadAttachList> list = new ArrayList<>();
        list.add(uploadAttachList);
        ResponseData responseData = new ResponseData(list);

        hlsWsRequests.setReturnStatus("200");
        hlsWsRequests.setResponseJson(JSONObject.toJSONString(responseData));
        logService.interfaceSave(hlsWsRequests,iRequest);

        return responseData;
    }
}