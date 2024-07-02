package com.hand.hls.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.dto.HapInterfaceHeader;
import com.hand.hap.intergration.dto.HapInterfaceLine;
import com.hand.hap.intergration.mapper.HapInterfaceHeaderMapper;
import com.hand.hap.intergration.mapper.HapInterfaceLineMapper;
import com.hand.hls.app.utils.WechatMessageSend;
import com.hand.hls.office.dto.FndAtmAttachmentDto;
import com.hand.hls.office.dto.FndAtmAttachmentMultiDto;
import com.hand.hls.office.mapper.FndAtmAttachmentMultiMapper;
import com.hand.hls.office.service.IFndAtmAttachmentMultiService;
import com.hand.hls.office.service.IFndAtmAttachmentService;
import com.hand.hls.service.IHlsFileUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaokai
 * @date 2021-7-13 11:38:09
 */
@Service
@Transactional
public class HlsFileUtilServiceImpl implements IHlsFileUtilService {
    /**
     * 请求地址
     */
    @Value("${file.upload.dir}")
    private String savePath;


    @Autowired
    private IFndAtmAttachmentService atmAttachmentService;
    @Autowired
    private IFndAtmAttachmentMultiService attachmentMultiService;

    @Autowired
    private FndAtmAttachmentMultiMapper attachmentMultiMapper;
    @Autowired
    HapInterfaceLineMapper hapInterfaceLineMapper;

    @Autowired
    HapInterfaceHeaderMapper hapInterfaceHeaderMapper;

    public Map<String, String> getWxinParams(String interfaceCode) {
        HapInterfaceHeader hapInterfaceHeader = new HapInterfaceHeader();
        hapInterfaceHeader.setInterfaceCode(interfaceCode);
        List<HapInterfaceHeader> allHeader = hapInterfaceHeaderMapper.getAllHeader(hapInterfaceHeader);

        if (!allHeader.isEmpty()) {
            hapInterfaceHeader = allHeader.get(0);
        }

        HapInterfaceLine hapInterfaceLine = new HapInterfaceLine();
        hapInterfaceLine.setHeaderId(hapInterfaceHeader.getHeaderId());
        List<HapInterfaceLine> interfaceLineList = hapInterfaceLineMapper.getLinesByHeaderId(hapInterfaceLine);

        Map<String, String> map = new HashMap<>();

        for (HapInterfaceLine item :
                interfaceLineList) {
            map.put(item.getLineCode(), item.getIftUrl());
        }
        return map;
    }


    @Override
    public String weChatDownloadsFile(String mediaId) throws Exception {

        Map<String, String> wxinParams = getWxinParams("GYZL_WX");

        String corpid = wxinParams.get("corpid");
        String corpsecret = wxinParams.get("Secret");

        JSONObject tokenObject = WechatMessageSend.getToken(corpid, corpsecret);
        String access_token = (String) tokenObject.get("access_token");

        String filePath = null;
        // 拼接请求地址
        String requestUrl = "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", access_token).replace("MEDIA_ID", mediaId);
        System.out.println(requestUrl);

            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");

            if (!savePath.endsWith("/")) {
                savePath += "/";
            }
            String contentType = conn.getHeaderField("Content-Type");
            // 根据内容类型获取扩展名
            String fileExt = getFileEndWitsh(contentType);

            File saveFile = new File(savePath);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            }

            // 将mediaId作为文件名
            if(fileExt == null || fileExt.length() <= 0){
                filePath = savePath + mediaId;
            }else{
                filePath = savePath + mediaId + fileExt;
            }

            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            byte[] buf = new byte[8096];
            int size = 0;
            while ((size = bis.read(buf)) != -1) {
                fos.write(buf, 0, size);
            }
            fos.close();
            bis.close();

            conn.disconnect();
            String info = String.format("下载文件文件成功，filePath=" + filePath);
            System.out.println(info);


        return filePath;
    }

    @Override
    public Long attchmentFileInsert(String filePath, String sourceTable, String fileName, Long id, IRequest iRequest) {
        //插入到附件表中
        Long attachmentId = 0L;
        File file = new File(filePath);
        String suffix;
        if(filePath.lastIndexOf(".") == -1){
            suffix = filePath;
        }else{
            suffix = filePath.substring(filePath.lastIndexOf("."), filePath.length());
        }
        long length = file.length();
        FndAtmAttachmentMultiDto attachmentMultiDto = new FndAtmAttachmentMultiDto();
        FndAtmAttachmentDto attachmentDto = new FndAtmAttachmentDto();
        attachmentMultiDto.setTableName(sourceTable);
        attachmentMultiDto.setTablePkValue(id);
        attachmentMultiDto.setCreationDate(new Date());
        attachmentMultiDto.setCreatedBy(iRequest.getUserId());
        attachmentMultiDto.setLastUpdateDate(new Date());
        attachmentMultiDto.setLastUpdatedBy(iRequest.getUserId());
        attachmentMultiDto.setTablePkValue(id);
        FndAtmAttachmentMultiDto multiDto = attachmentMultiService.insertSelective(iRequest, attachmentMultiDto);
        attachmentDto.setSourceTypeCode("fnd_atm_attachment_multi");
        attachmentDto.setFileTypeCode(suffix);
        attachmentDto.setSourcePkValue(String.valueOf(multiDto.getRecordId()));

        fileName= fileName+"."+suffix;
        attachmentDto.setFileName(fileName);
        attachmentDto.setFileSize(length);
        attachmentDto.setFilePath(filePath);
        attachmentDto.setCreationDate(new Date());
        attachmentDto.setCreatedBy(iRequest.getUserId());
        attachmentDto.setLastUpdateDate(new Date());
        attachmentDto.setLastUpdatedBy(iRequest.getUserId());
        attachmentDto = atmAttachmentService.insertSelective(iRequest, attachmentDto);
        multiDto.setAttachmentId(attachmentDto.getAttachmentId());
        attachmentId = attachmentDto.getAttachmentId();
        attachmentMultiService.updateByPrimaryKeySelective(iRequest, multiDto);
        return attachmentId;

    }

    @Override
    public void attchmentFileDelete(Long attchmentId, IRequest iRequest) {
        FndAtmAttachmentDto fndAttachment = new FndAtmAttachmentDto();
        fndAttachment.setAttachmentId(attchmentId);
        fndAttachment = atmAttachmentService.selectByPrimaryKey(iRequest, fndAttachment);
        FndAtmAttachmentMultiDto fndAtmAttachmentMultiDto = new FndAtmAttachmentMultiDto();
        fndAtmAttachmentMultiDto.setAttachmentId(fndAttachment.getAttachmentId());

        List<FndAtmAttachmentMultiDto> attachmentMultiDtoList = attachmentMultiMapper.select(fndAtmAttachmentMultiDto);
        //删除附件信息  再删除附件
        atmAttachmentService.deleteByPrimaryKey(fndAttachment);

        for (FndAtmAttachmentMultiDto item:attachmentMultiDtoList
             ) {
            attachmentMultiService.deleteByPrimaryKey(item);
        }

        File file = new File(fndAttachment.getFilePath());
        if (file.exists()){
            file.delete();
        }

    }


    static Map<String, String> map = new HashMap<String, String>();

    static {

        map.put("image/jpeg", ".jpg");

        map.put("audio/mp3", ".mp3");

        map.put("video/mpeg4", ".mp4");

    }

    private static String getFileEndWitsh(String contentType) {

        return map.get(contentType);

    }
}
