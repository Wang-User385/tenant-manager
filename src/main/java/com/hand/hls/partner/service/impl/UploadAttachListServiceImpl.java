package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.partner.mapper.UploadAttachListMapper;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.hand.hls.partner.dto.UploadAttachList;
import com.hand.hls.partner.service.IUploadAttachListService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class UploadAttachListServiceImpl extends BaseServiceImpl<UploadAttachList> implements IUploadAttachListService{

    @Autowired
    private IHlsWsRequestsService logService;
    @Value("${file.upload.dir:.}")
    private String savePath = ".";
    @Autowired
    private UploadAttachListMapper uploadAttachListMapper;
    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Override
    public String getUploadUrl(String decryptedStr){
        UploadAttachList uploadAttachList = JSONObject.parseObject(decryptedStr, UploadAttachList.class);
        String fileId = UUID.randomUUID().toString();
        String uploadUrl = "/r/api/di/upload?fileId=" + fileId;
        uploadAttachList.setFileId(fileId);
        uploadAttachList.setUploadUrl(uploadUrl);
        this.insertSelective(RequestHelper.getCurrentRequest(),uploadAttachList);

        JSONObject resJson = new JSONObject();
        resJson.put("fileId",fileId);
        resJson.put("uploadUrl",uploadUrl);
        return JSONObject.toJSONString(resJson);
    }

    @Override
    public String upload(String fileId, MultipartFile file){
        JSONObject resJson = new JSONObject();
        UploadAttachList uploadAttachList = uploadAttachListMapper.selectByFileId(fileId);
        if(uploadAttachList == null){
            resJson.put("success",false);
            resJson.put("message","fileId错误！");
            return JSONObject.toJSONString(resJson);
        }
        if("Y".equals(uploadAttachList.getUploadFlag())){
            resJson.put("success",false);
            resJson.put("message","文件已上传，不能重复上传！");
            return JSONObject.toJSONString(resJson);
        }
        if(file.isEmpty()){
            resJson.put("success",false);
            resJson.put("message","文件不存在！");
            return JSONObject.toJSONString(resJson);
        }
        //存储附件
        String filename = file.getOriginalFilename();
        String path = this.savePath + File.separator + fileId;
        try {
            File dest = new File(path); // 创建目标文件对象
            file.transferTo(dest); // 将上传的文件保存到目标位置
        } catch (IOException e) {
            e.printStackTrace();
            resJson.put("success",false);
            resJson.put("message","文件上传失败！");
            return JSONObject.toJSONString(resJson);
        }
        //存储附件相关表
        try {
            fndAttachmentService.uploadAttachment(URLDecoder.decode(filename, "UTF-8"), path, "GT_UPLOAD_ATTACH_LIST", uploadAttachList.getListId().toString(), file.getSize());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resJson.put("success",false);
            resJson.put("message","文件上传失败！");
            return JSONObject.toJSONString(resJson);
        }
        //设置为已上传
        uploadAttachList.setUploadFlag("Y");
        uploadAttachListMapper.updateByPrimaryKeySelective(uploadAttachList);

        resJson.put("success",true);
        resJson.put("message","文件上传成功！");
        return JSONObject.toJSONString(resJson);
    }

}