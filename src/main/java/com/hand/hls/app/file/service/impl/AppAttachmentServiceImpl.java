package com.hand.hls.app.file.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.app.file.service.IAppAttachmentService;
import com.hand.hls.app.utils.generalUtils.AppCheckRequiredUtils;
import com.hand.hls.app.utils.generalUtils.AppConstantUtils;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.service.IFndAttachmentService;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import uncertain.composite.CompositeMap;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * @author liao
 */
@Service
public class AppAttachmentServiceImpl extends BaseServiceImpl<FndAttachment> implements IAppAttachmentService {
    @Autowired
    private IFndAttachmentService fndAttachmentService;

    @Value("${file.upload.dir:.}")
    private String savePath = ".";
    private boolean needParse = false;

    /**
     * queryAttachment 必输字段
     */
    private final static List<String> QUERY_ATTACH_MENT = Arrays.asList("docAttachmentId","tableName");
    /**
     * downloadAtt 必输字段
     */
    private final static List<String> DOWNLOAD_ATT = Arrays.asList("fileId");
    /**
     * deleteAtt 必输字段
     */
    private final static List<String> DELETE_ATT = Arrays.asList("attachmentId");

    @Override
    public ResponseData queryAttachment(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();

        try {

            responseData = AppCheckRequiredUtils.checkRequired(jsonObject,QUERY_ATTACH_MENT);
            if (!responseData.isSuccess()) {
                return responseData;
            }
            Map parameter = jsonObject.getInnerMap();
            CompositeMap map = new CompositeMap("parameter",parameter );
            map.put("header_id",jsonObject.getString("docAttachmentId"));
            map.put("table_name",jsonObject.getString("tableName"));

            CompositeMap root = new CompositeMap();
            root.addChild(map);
            responseData.setRows(fndAttachmentService.queryAttachment(root, null));
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseEntity<byte[]> downloadAtt(IRequest iRequest, JSONObject jsonObject) {

        try {


            ResponseData  checkRequired = AppCheckRequiredUtils.checkRequired(jsonObject,DOWNLOAD_ATT);
            if (!checkRequired.isSuccess()) {
                return ResponseEntity.status(HttpStatus.valueOf(checkRequired.getMessage())).build();
            }

            FndAttachment fndAttachment = new FndAttachment();
            fndAttachment.setAttachmentId(jsonObject.getLong("fileId")  );
            FndAttachment attachment = fndAttachmentService.selectByPrimaryKey(iRequest, fndAttachment);
            if (attachment != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", new String(attachment.getFileName().getBytes(), StandardCharsets.ISO_8859_1));
                headers.setContentLength(attachment.getFileSize());
                String filePath = attachment.getFilePath();
                File file = new File(filePath);
                if(file.exists()){
                    byte[] bytes = FileUtils.readFileToByteArray(file);
                    return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
                }
            }

        } catch (Exception e) {

            throw new ActivitiIllegalArgumentException("Error exporting diagram", e);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseData deleteAtt(IRequest iRequest, JSONObject jsonObject) {
        ResponseData responseData = new ResponseData();

        try {

            responseData = AppCheckRequiredUtils.checkRequired(jsonObject,DELETE_ATT);
            if (!responseData.isSuccess()) {
                return responseData;
            }

            Long attachmentId = jsonObject.getLong("attachmentId");
            fndAttachmentService.deleteAttachment(attachmentId);
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData uploadFile(HttpServletRequest request, IRequest iRequest) {
        ResponseData responseData = new ResponseData();

        try {
            RequestHelper.setCurrentRequest(iRequest);
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
//        multipartResolver.setDefaultEncoding("UTF-8");
            if (!multipartResolver.isMultipart(request)) {
                return new ResponseData(false);
            }
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

            String sourceType = multiRequest.getParameter("sourceType");
            String pkValue = multiRequest.getParameter("pkvalue");
            String filename = multiRequest.getParameter("filename");

            Iterator iter = multiRequest.getFileNames();
            Long attachmentId = null;
            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file != null) {
                    String path = getSavePath();
                    File target = new File(path);
                    file.transferTo(target);
                    attachmentId = fndAttachmentService.uploadAttachment(URLDecoder.decode(filename, "UTF-8"), path, sourceType, pkValue, file.getSize());
                }
            }


            responseData.setRows(new ArrayList<>(Arrays.asList(attachmentId)));
            responseData.setSuccess(true);
            responseData.setMessage(AppConstantUtils.PublicMsg.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }
        return responseData;
    }

    private String getSavePath() {
        String filePath = savePath;
        if(needParse){
            final LocalDateTime now = LocalDateTime.now();
            filePath = savePath.replace("{yyyy}", String.valueOf(now.getYear()))
                    .replace("{MM}", String.valueOf(now.getMonth()))
                    .replace("{dd}", String.valueOf(now.getDayOfMonth()));
        }
        return filePath + File.separator + UUID.randomUUID().toString();
    }

}
