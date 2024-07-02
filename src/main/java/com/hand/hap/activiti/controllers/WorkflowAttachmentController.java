package com.hand.hap.activiti.controllers;

import java.io.File;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import com.hand.hap.activiti.util.EncryptUtils;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.oss.service.CloudStorageService;
import com.hand.hls.oss.utils.OSSUtils;
import com.hand.hls.prj.utils.CommonException;
import leaf.utils.BrowserUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * description
 *
 * @author Lenovo 2023/03/30 14:43
 */
@Controller
public class WorkflowAttachmentController extends BaseController implements InitializingBean {

    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Value("${file.upload.dir:.}")
    private String savePath = ".";
    @Autowired(required = false)
    private CloudStorageService cloudStorageService;
    @Value("${workflow.encryptStr:}")
    private String encryptKey;
    private boolean needParse = false;

    public static final String PUBLIC_DOWNLOAD = "/api/public/download";


    @RequestMapping(value = {PUBLIC_DOWNLOAD})
    @ResponseBody
    public ResponseEntity<byte[]> downloadAttachment(HttpServletRequest request, @RequestParam("attachment_id") String attachmentIdEnc) throws Exception {
        String decrypt = EncryptUtils.decrypt(encryptKey, attachmentIdEnc);
        if(StringUtils.isEmpty(decrypt)){
            throw new CommonException("文件解密失败!");
        }
        long attachmentId = Long.parseLong(decrypt);
        IRequest requestContext = this.createRequestContext(request);
        FndAttachment fndAttachment = new FndAttachment();
        fndAttachment.setAttachmentId(attachmentId);
        FndAttachment attachment = this.fndAttachmentService.selectByPrimaryKey(requestContext, fndAttachment);
        if (attachment != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", BrowserUtils.getFileName(request, attachment.getFileName()));
            headers.setContentLength(attachment.getFileSize());
            String filePath = attachment.getFilePath();
            byte[] bytes;
            if (OSSUtils.isOOSFile(filePath) && this.cloudStorageService != null) {
                InputStream download = this.cloudStorageService.download(filePath);
                bytes = IOUtils.toByteArray(download);
                IOUtils.closeQuietly(download);
                return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
            }

            File file = new File(filePath);
            if (file.exists()) {
                bytes = FileUtils.readFileToByteArray(file);
                return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isBlank(this.savePath)) {
            throw new RuntimeException("Config file.upload.dir cannot be blank.");
        } else {
            File file = new File(this.savePath);
            if (!file.exists()) {
                file.mkdirs();
            }

            if (this.savePath.contains("{yyyy}") || this.savePath.contains("{MM}") || this.savePath.contains("{dd}")) {
                this.needParse = true;
            }

        }
    }
}
